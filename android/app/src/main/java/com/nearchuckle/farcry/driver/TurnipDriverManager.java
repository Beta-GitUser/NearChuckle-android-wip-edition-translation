package com.nearchuckle.farcry.driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Manages custom Turnip Vulkan driver ZIP installation, extraction, and configuration.
 */
public class TurnipDriverManager {
    private static final String TAG = "TurnipDriverManager";
    private static final String PREF_NAME = "farcry_drivers";
    private static final String KEY_DRIVERS_JSON = "installed_drivers_json";
    private static final String KEY_SELECTED_DRIVER = "selected_driver_id";
    private static final String KEY_TURBO_ENABLED = "enable_gpu_turbo";

    public static List<DriverInfo> getInstalledDrivers(Context context) {
        List<DriverInfo> list = new ArrayList<>();
        list.add(DriverInfo.createSystemDriver());

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonStr = prefs.getString(KEY_DRIVERS_JSON, null);
        if (jsonStr != null && !jsonStr.isEmpty()) {
            try {
                JSONArray arr = new JSONArray(jsonStr);
                for (int i = 0; i < arr.length(); i++) {
                    DriverInfo info = DriverInfo.fromJson(arr.getJSONObject(i));
                    // Verify files still exist
                    File driverDir = new File(info.getInstalledPath());
                    if (driverDir.exists() && driverDir.isDirectory()) {
                        list.add(info);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse installed drivers json", e);
            }
        }
        return list;
    }

    public static DriverInfo getSelectedDriver(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String selectedId = prefs.getString(KEY_SELECTED_DRIVER, DriverInfo.DRIVER_SYSTEM_ID);

        List<DriverInfo> installed = getInstalledDrivers(context);
        for (DriverInfo info : installed) {
            if (info.getId().equals(selectedId)) {
                return info;
            }
        }
        return DriverInfo.createSystemDriver();
    }

    public static void setSelectedDriver(Context context, String driverId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_SELECTED_DRIVER, driverId).apply();
    }

    public static boolean isTurboEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_TURBO_ENABLED, false);
    }

    public static void setTurboEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_TURBO_ENABLED, enabled).apply();
    }

    /**
     * Unzips and registers a Turnip driver package from URI.
     */
    public static DriverInfo installDriverFromUri(Context context, Uri uri, String fileName) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(uri);
        if (is == null) {
            throw new IOException("Cannot open input stream for URI: " + uri);
        }
        try {
            return installDriverFromStream(context, is, fileName);
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {}
        }
    }

    /**
     * Unzips and registers a Turnip driver package from an InputStream.
     */
    public static DriverInfo installDriverFromStream(Context context, InputStream is, String fileName) throws IOException {
        String driverId = "turnip_" + UUID.randomUUID().toString().substring(0, 8);
        File driversRoot = new File(context.getFilesDir(), "drivers");
        File targetDir = new File(driversRoot, driverId);
        if (!targetDir.mkdirs() && !targetDir.isDirectory()) {
            throw new IOException("Failed to create driver directory: " + targetDir.getAbsolutePath());
        }

        String metaJsonContent = null;
        String detectedSoName = null;

        byte[] buffer = new byte[8192];
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                // Normalize and sanitize against path traversal
                entryName = entryName.replace('\\', '/');
                if (entryName.contains("..")) {
                    continue;
                }

                // If file is inside a subfolder, flatten or preserve relative path
                File outputFile = new File(targetDir, entryName);
                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                    continue;
                }

                File parent = outputFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }

                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }

                if (outputFile.getName().equalsIgnoreCase("meta.json")) {
                    metaJsonContent = readFileToString(outputFile);
                } else if (outputFile.getName().endsWith(".so")) {
                    if (outputFile.getName().contains("freedreno") || detectedSoName == null) {
                        detectedSoName = outputFile.getName();
                    }
                }
            }
        }

        DriverInfo info = new DriverInfo();
        info.setId(driverId);
        info.setInstalledPath(targetDir.getAbsolutePath());

        if (metaJsonContent != null) {
            try {
                JSONObject json = new JSONObject(metaJsonContent);
                info.setName(json.optString("name", "Turnip Driver"));
                info.setDescription(json.optString("description", "Mesa Turnip Vulkan Driver"));
                info.setAuthor(json.optString("author", "Freedreno"));
                info.setVendor(json.optString("vendor", "Mesa"));
                info.setDriverVersion(json.optString("driverVersion", "Custom"));
                info.setPackageVersion(json.optString("packageVersion", "1.0"));
                info.setMinApi(json.optInt("minApi", 26));
                String libName = json.optString("libraryName", null);
                if (libName != null && !libName.isEmpty()) {
                    info.setLibraryName(libName);
                } else {
                    info.setLibraryName(detectedSoName != null ? detectedSoName : "libvulkan_freedreno.so");
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to parse meta.json, using fallback", e);
                setDefaultDriverInfo(info, fileName, detectedSoName);
            }
        } else {
            setDefaultDriverInfo(info, fileName, detectedSoName);
        }

        // Verify driver library file exists in target dir
        File mainLib = new File(targetDir, info.getLibraryName());
        if (!mainLib.exists()) {
            // Check if it's in a subdirectory
            File[] files = targetDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".so")) {
                        info.setLibraryName(f.getName());
                        break;
                    }
                }
            }
        }

        // Generate Vulkan ICD JSON for the driver
        writeIcdJson(targetDir, info.getLibraryName());

        // Save to SharedPreferences
        saveDriver(context, info);

        return info;
    }

    private static void setDefaultDriverInfo(DriverInfo info, String fileName, String detectedSoName) {
        String cleanName = fileName != null ? fileName : "Turnip Driver";
        if (cleanName.toLowerCase().endsWith(".zip")) {
            cleanName = cleanName.substring(0, cleanName.length() - 4);
        }
        info.setName(cleanName);
        boolean isRu = java.util.Locale.getDefault().getLanguage().equals("ru");
        info.setDescription(isRu ? ("Пользовательский Turnip Vulkan драйвер из архива " + fileName)
                                 : ("Custom Turnip Vulkan driver from " + fileName));
        info.setAuthor("Mesa / Freedreno");
        info.setVendor("Mesa");
        info.setDriverVersion("Custom");
        info.setPackageVersion("1.0");
        info.setMinApi(26);
        info.setLibraryName(detectedSoName != null ? detectedSoName : "libvulkan_freedreno.so");
    }

    private static void writeIcdJson(File targetDir, String libName) {
        File icdFile = new File(targetDir, "turnip_icd.json");
        try (FileWriter writer = new FileWriter(icdFile)) {
            JSONObject root = new JSONObject();
            root.put("file_format_version", "1.0.0");
            JSONObject icd = new JSONObject();
            icd.put("library_path", new File(targetDir, libName).getAbsolutePath());
            icd.put("api_version", "1.3.0");
            root.put("ICD", icd);
            writer.write(root.toString(2));
        } catch (Exception e) {
            Log.e(TAG, "Failed to write turnip_icd.json", e);
        }
    }

    public static File getIcdJsonFile(DriverInfo info) {
        if (info == null || info.isSystem() || info.getInstalledPath() == null) {
            return null;
        }
        return new File(info.getInstalledPath(), "turnip_icd.json");
    }

    private static void saveDriver(Context context, DriverInfo newDriver) {
        List<DriverInfo> current = getInstalledDrivers(context);
        JSONArray arr = new JSONArray();
        for (DriverInfo info : current) {
            if (!info.isSystem() && !info.getId().equals(newDriver.getId())) {
                try {
                    arr.put(info.toJson());
                } catch (Exception ignored) {}
            }
        }
        try {
            arr.put(newDriver.toJson());
        } catch (Exception ignored) {}

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_DRIVERS_JSON, arr.toString()).apply();
    }

    public static boolean deleteDriver(Context context, String driverId) {
        if (DriverInfo.DRIVER_SYSTEM_ID.equals(driverId)) {
            return false;
        }

        List<DriverInfo> current = getInstalledDrivers(context);
        JSONArray arr = new JSONArray();
        String pathToDelete = null;

        for (DriverInfo info : current) {
            if (!info.isSystem()) {
                if (info.getId().equals(driverId)) {
                    pathToDelete = info.getInstalledPath();
                } else {
                    try {
                        arr.put(info.toJson());
                    } catch (Exception ignored) {}
                }
            }
        }

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_DRIVERS_JSON, arr.toString()).apply();

        if (driverId.equals(prefs.getString(KEY_SELECTED_DRIVER, ""))) {
            prefs.edit().putString(KEY_SELECTED_DRIVER, DriverInfo.DRIVER_SYSTEM_ID).apply();
        }

        if (pathToDelete != null) {
            deleteRecursive(new File(pathToDelete));
            return true;
        }
        return false;
    }

    private static void deleteRecursive(File f) {
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        f.delete();
    }

    private static String readFileToString(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading file " + file.getAbsolutePath(), e);
        }
        return sb.toString();
    }
}
