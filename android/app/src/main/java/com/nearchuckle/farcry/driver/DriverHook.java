package com.nearchuckle.farcry.driver;

import android.content.Context;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import java.io.File;

/**
 * JNI wrapper for custom Turnip driver loading and adrenotools rootless hooks.
 */
public class DriverHook {
    private static final String TAG = "NearChuckle-DriverHook";
    private static boolean sLibraryLoaded = false;

    static {
        try {
            System.loadLibrary("driverloader");
            sLibraryLoaded = true;
            Log.i(TAG, "libdriverloader.so loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            Log.w(TAG, "libdriverloader.so not available: " + e.getMessage());
            sLibraryLoaded = false;
        }
    }

    public static native boolean nativeInitDriver(
            String hookLibDir,
            String customDriverDir,
            String customDriverName,
            boolean turbo
    );

    public static native void nativeSetTurbo(boolean turbo);

    public static native boolean nativeIsAdreno();

    public static native boolean nativeCheckSupport();

    public static boolean isSupported() {
        if (!sLibraryLoaded) return false;
        try {
            return nativeCheckSupport();
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isQualcommAdreno() {
        if (sLibraryLoaded) {
            try {
                return nativeIsAdreno();
            } catch (Throwable ignored) {}
        }
        File kgsl = new File("/dev/kgsl-3d0");
        return kgsl.exists();
    }

    /**
     * Applies the selected driver and turbo settings to the running process.
     */
    public static boolean apply(Context context, DriverInfo driver, boolean turbo) {
        String hookLibDir = context.getApplicationInfo().nativeLibraryDir;

        if (driver == null || driver.isSystem()) {
            Log.i(TAG, "Applying System Default Vulkan Driver");
            try {
                // Clear any custom ICD override
                Os.unsetenv("VK_ICD_FILENAMES");
            } catch (Exception ignored) {}

            if (sLibraryLoaded) {
                try {
                    nativeInitDriver(hookLibDir, null, null, turbo);
                } catch (Throwable t) {
                    Log.w(TAG, "Error calling nativeInitDriver for system driver", t);
                }
            }
            return true;
        }

        String driverDir = driver.getInstalledPath();
        String driverName = driver.getLibraryName();
        File driverFile = new File(driverDir, driverName);

        Log.i(TAG, "Applying Custom Turnip Driver: " + driverFile.getAbsolutePath() + ", turbo=" + turbo);

        // 1. Point VK_ICD_FILENAMES to the generated ICD JSON
        File icdJson = TurnipDriverManager.getIcdJsonFile(driver);
        if (icdJson != null && icdJson.exists()) {
            try {
                Os.setenv("VK_ICD_FILENAMES", icdJson.getAbsolutePath(), true);
                Log.i(TAG, "Set VK_ICD_FILENAMES to: " + icdJson.getAbsolutePath());
            } catch (ErrnoException e) {
                Log.w(TAG, "Failed to set VK_ICD_FILENAMES", e);
            }
        }

        // 2. Set driver search paths for Mesa Zink
        try {
            Os.setenv("LIBGL_DRIVERS_PATH", driverDir, true);
        } catch (Exception ignored) {}

        // 3. Call adrenotools rootless hook in native code
        if (sLibraryLoaded) {
            try {
                boolean result = nativeInitDriver(hookLibDir, driverDir, driverName, turbo);
                Log.i(TAG, "nativeInitDriver result: " + result);
                return result;
            } catch (Throwable t) {
                Log.e(TAG, "Failed calling nativeInitDriver", t);
            }
        }

        return true;
    }
}
