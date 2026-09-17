package com.nearchuckle.farcry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.nearchuckle.farcry.controls.OscManager;
import com.nearchuckle.farcry.driver.DriverHook;
import com.nearchuckle.farcry.driver.DriverInfo;
import com.nearchuckle.farcry.driver.TurnipDriverManager;

import org.libsdl.app.SDLActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main game execution activity extending SDLActivity.
 * Configures Mesa Zink, Turnip custom drivers, working paths, and overlays touch controls.
 */
public class GameActivity extends SDLActivity {
    private static final String TAG = "NearChuckle-GameActivity";
    private OscManager oscManager;

    @Override
    protected String[] getLibraries() {
        return new String[]{
                "c++_shared",
                "SDL3",
                "FarCry"
        };
    }

    @Override
    public void loadLibraries() {
        Context context = this;
        SharedPreferences prefs = context.getSharedPreferences(LauncherActivity.PREFS_NAME, MODE_PRIVATE);

        String defaultPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/FarCry";
        String gamePath = prefs.getString(LauncherActivity.KEY_GAME_PATH, defaultPath);
        boolean useZink = prefs.getBoolean(LauncherActivity.KEY_USE_ZINK, true);
        boolean turbo = TurnipDriverManager.isTurboEnabled(context);
        DriverInfo selectedDriver = TurnipDriverManager.getSelectedDriver(context);

        Log.i(TAG, "Initializing Far Cry Android Port...");
        Log.i(TAG, "Game Path: " + gamePath);
        Log.i(TAG, "Renderer Mesa Zink: " + useZink);
        Log.i(TAG, "GPU Driver: " + (selectedDriver != null ? selectedDriver.getName() : "System Default"));

        // 1. Set Far Cry Working directory and Module search path
        String nativeLibDir = getApplicationInfo().nativeLibraryDir;
        try {
            if (!gamePath.isEmpty()) {
                Os.setenv("FARCRY_DATA_DIR", gamePath, true);
            }
            Os.setenv("MODULE_PATH", nativeLibDir + "/", true);
        } catch (ErrnoException e) {
            Log.e(TAG, "Failed setting path environment variables", e);
        }

        // 2. Configure Mesa Zink (OpenGL over Vulkan)
        if (useZink) {
            try {
                Os.setenv("MESA_LOADER_DRIVER_OVERRIDE", "zink", true);
                Os.setenv("GALLIUM_DRIVER", "zink", true);
                Os.setenv("ZINK_DESCRIPTORS", "lazy", true);
                Os.setenv("MESA_GL_VERSION_OVERRIDE", "2.1", true);
                Os.setenv("MESA_GLSL_VERSION_OVERRIDE", "140", true);
                // Allow ARB shaders for Far Cry CryEngine 1
                Os.setenv("MESA_EXTENSION_OVERRIDE", "+GL_ARB_vertex_program +GL_ARB_fragment_program", true);
                Log.i(TAG, "Configured Mesa Zink environment variables.");
            } catch (ErrnoException e) {
                Log.e(TAG, "Failed setting Zink environment variables", e);
            }
        }

        // 3. Preload libc++_shared so native dependencies are resolved
        try {
            System.loadLibrary("c++_shared");
        } catch (Throwable t) {
            Log.w(TAG, "libc++_shared pre-load: " + t.getMessage());
        }

        // 4. Configure Turnip / Custom Vulkan driver via adrenotools
        try {
            DriverHook.apply(context, selectedDriver, turbo);
        } catch (Throwable t) {
            Log.e(TAG, "Error applying GPU driver hook", t);
        }

        // 5. Load native libraries (c++_shared, SDL3, FarCry)
        super.loadLibraries();
    }

    @Override
    protected String[] getArguments() {
        SharedPreferences prefs = getSharedPreferences(LauncherActivity.PREFS_NAME, MODE_PRIVATE);
        List<String> args = new ArrayList<>();

        // Base executable name
        args.add("FarCry");

        // Devmode
        if (prefs.getBoolean(LauncherActivity.KEY_DEVMODE, false)) {
            args.add("-DEVMODE");
        }

        // Renderer
        args.add("r_Driver=OpenGL");

        // FOV
        int fov = prefs.getInt(LauncherActivity.KEY_FOV, 90);
        args.add("game_fov=" + fov);

        // Resolution
        int resMode = prefs.getInt(LauncherActivity.KEY_RES_MODE, 0);
        if (resMode == 1) { // 1080p
            args.add("r_Width=1920");
            args.add("r_Height=1080");
        } else if (resMode == 2) { // 720p
            args.add("r_Width=1280");
            args.add("r_Height=720");
        } else if (resMode == 3) { // 540p
            args.add("r_Width=960");
            args.add("r_Height=540");
        }

        // Custom parameters
        String customArgs = prefs.getString(LauncherActivity.KEY_CUSTOM_ARGS, "").trim();
        if (!customArgs.isEmpty()) {
            String[] split = customArgs.split("\\s+");
            for (String arg : split) {
                if (!arg.isEmpty()) {
                    args.add(arg);
                }
            }
        }

        return args.toArray(new String[0]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CrashHandler.init(this);
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUI();

        // Attach On-Screen Controls (OSC) to SDL layout
        setupControlsOverlay();
    }

    private void setupControlsOverlay() {
        if (mLayout == null) return;

        SharedPreferences prefs = getSharedPreferences(LauncherActivity.PREFS_NAME, MODE_PRIVATE);
        boolean hideControls = prefs.getBoolean(LauncherActivity.KEY_HIDE_CONTROLS, false);

        RelativeLayout oscContainer = new RelativeLayout(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        oscContainer.setLayoutParams(lp);

        oscManager = new OscManager();
        oscManager.init(oscContainer, this, false);

        if (hideControls) {
            oscContainer.setVisibility(View.GONE);
        }

        mLayout.addView(oscContainer);
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure clean exit of native engine
        Process.killProcess(Process.myPid());
    }
}
