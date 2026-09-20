package com.nearchuckle.farcry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Self-update helper for the NearChuckle launcher.
 *
 * <p>The launcher asks a small JSON manifest ("update.json") which version is the newest one and
 * where its APK can be downloaded. When the manifest advertises a {@code versionCode} higher than
 * the installed build, the launcher shows a dialog with two buttons: "Download" and "Cancel".</p>
 *
 * <p>Manifest format (see {@code android/update.json} in this repository):</p>
 * <pre>
 * {
 *   "versionCode": 101,          // required, must match android/app/build.gradle versionCode
 *   "versionName": "1.0.2",      // optional, shown to the user
 *   "apkUrl": "https://...",     // optional, falls back to the newest GitHub release APK asset
 *   "size": 10500784,            // optional, APK size in bytes (progress bar fallback)
 *   "notes": "What is new...",   // optional, changelog shown in the dialog
 *   "mandatory": false           // optional, true = the dialog cannot be dismissed with Back
 * }
 * </pre>
 */
public final class UpdateManager {

    public static final String TAG = "FarCryUpdate";

    /** Authority suffix of the FileProvider declared in AndroidManifest.xml. */
    public static final String FILE_PROVIDER_SUFFIX = ".fileprovider";

    public static final String APK_MIME = "application/vnd.android.package-archive";

    /** Manifest locations, tried in order. The first one that answers wins. */
    public static final String[] MANIFEST_URLS = {
            // Project file host (the same host that serves the GL shader cache).
            "https://rohitcodes.fyi/nearchuckle/update.json",
            // Copy kept in the repository - works without any extra hosting.
            "https://raw.githubusercontent.com/Player124413/NearChuckle-android-edition/Android/android/update.json"
    };

    /** Used to find the APK download link when the manifest does not contain "apkUrl". */
    public static final String GITHUB_LATEST_RELEASE_URL =
            "https://api.github.com/repos/Player124413/NearChuckle-android-edition/releases/latest";

    /** How long a fetched manifest stays valid before the launcher hits the network again. */
    public static final long CACHE_TTL_MS = 6 * 60 * 60 * 1000L;

    public static final String KEY_LAST_CHECK_MS = "update_last_check_ms";
    public static final String KEY_CACHED_MANIFEST = "update_cached_manifest";
    public static final String KEY_CACHED_SOURCE = "update_cached_manifest_source";

    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final int READ_TIMEOUT_MS = 30000;
    private static final String USER_AGENT = "NearChuckle-Launcher/1.0 (Android)";

    private UpdateManager() {
    }

    // ---------------------------------------------------------------------------------------------
    // Update manifest
    // ---------------------------------------------------------------------------------------------

    /** Parsed contents of the remote update manifest. */
    public static final class UpdateInfo {
        public final int versionCode;
        @Nullable
        public final String versionName;
        @Nullable
        public final String apkUrl;
        @Nullable
        public final String notes;
        public final long sizeBytes;
        public final boolean mandatory;
        /** URL the manifest was read from (for logs / caching). */
        @Nullable
        public final String source;

        UpdateInfo(int versionCode, @Nullable String versionName, @Nullable String apkUrl,
                   @Nullable String notes, long sizeBytes, boolean mandatory, @Nullable String source) {
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.apkUrl = apkUrl;
            this.notes = notes;
            this.sizeBytes = sizeBytes;
            this.mandatory = mandatory;
            this.source = source;
        }

        public UpdateInfo withApkUrl(String url) {
            return new UpdateInfo(versionCode, versionName, url, notes, sizeBytes, mandatory, source);
        }

        public boolean hasApkUrl() {
            return apkUrl != null && !apkUrl.trim().isEmpty();
        }

        /** Parses the manifest document. Throws when it is not usable. */
        public static UpdateInfo parse(String json, @Nullable String source) throws JSONException {
            if (json == null || json.trim().isEmpty()) {
                throw new JSONException("empty update manifest");
            }
            JSONObject o = new JSONObject(json.trim());
            int code = o.optInt("versionCode", -1);
            if (code <= 0) {
                throw new JSONException("update manifest has no valid \"versionCode\"");
            }
            return new UpdateInfo(
                    code,
                    optTrimmed(o, "versionName"),
                    optTrimmed(o, "apkUrl"),
                    optTrimmed(o, "notes"),
                    o.optLong("size", 0L),
                    o.optBoolean("mandatory", false),
                    source);
        }

        /** Serializes back to JSON so the manifest can be cached in SharedPreferences. */
        public String toJson() {
            JSONObject o = new JSONObject();
            try {
                o.put("versionCode", versionCode);
                if (versionName != null) o.put("versionName", versionName);
                if (apkUrl != null) o.put("apkUrl", apkUrl);
                if (notes != null) o.put("notes", notes);
                if (sizeBytes > 0) o.put("size", sizeBytes);
                o.put("mandatory", mandatory);
                if (source != null) o.put("source", source);
            } catch (JSONException ignored) {
            }
            return o.toString();
        }

        @Nullable
        private static String optTrimmed(JSONObject o, String key) {
            String v = o.optString(key, null);
            if (v == null) return null;
            v = v.trim();
            return v.isEmpty() ? null : v;
        }

        @Override
        public String toString() {
            return "UpdateInfo{" + versionName + " (" + versionCode + ") from " + source + "}";
        }
    }

    /** Result of an update check, delivered on the main thread. */
    public interface Callback {
        /**
         * @param info  a newer version, or {@code null} when the app is up to date
         * @param error human readable error, or {@code null} when the check itself succeeded
         */
        void onResult(@Nullable UpdateInfo info, @Nullable String error);
    }

    /** Download progress / result callbacks, invoked on a background thread. */
    public interface DownloadListener {
        void onProgress(int percent, long downloadedBytes, long totalBytes);

        void onSuccess(File apk);

        void onError(Exception e);
    }

    // ---------------------------------------------------------------------------------------------
    // Version helpers
    // ---------------------------------------------------------------------------------------------

    public static int getInstalledVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return (int) pi.getLongVersionCode();
            }
            return pi.versionCode;
        } catch (Exception e) {
            Log.w(TAG, "Cannot read installed versionCode", e);
            return 0;
        }
    }

    public static String getInstalledVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (pi.versionName != null && !pi.versionName.isEmpty()) {
                return pi.versionName;
            }
        } catch (Exception ignored) {
        }
        return String.valueOf(getInstalledVersionCode(context));
    }

    // ---------------------------------------------------------------------------------------------
    // Checking
    // ---------------------------------------------------------------------------------------------

    /**
     * Looks for a newer build in the background and calls back on the main thread.
     *
     * @param force ignore the local manifest cache and always hit the network
     */
    public static void checkAsync(Context context, final boolean force, final Callback callback) {
        final Context app = context.getApplicationContext();
        final Handler main = new Handler(Looper.getMainLooper());
        final int installedCode = getInstalledVersionCode(app);

        Thread worker = new Thread(() -> {
            UpdateInfo fetched = null;
            String error = null;
            try {
                fetched = loadUpdateInfo(app, force);
            } catch (Exception e) {
                error = describe(e);
                Log.w(TAG, "Update check failed", e);
            }

            final UpdateInfo result =
                    (fetched != null && fetched.versionCode > installedCode) ? fetched : null;
            final String failure = error;
            if (result != null) {
                Log.i(TAG, "Update available: " + result + ", installed code=" + installedCode);
            }
            main.post(() -> callback.onResult(result, failure));
        }, "NearChuckle-UpdateCheck");
        worker.setDaemon(true);
        worker.start();
    }

    /** Reads the manifest, preferring a fresh local cache unless {@code force} is set. */
    @Nullable
    private static UpdateInfo loadUpdateInfo(Context app, boolean force) throws Exception {
        SharedPreferences prefs = app.getSharedPreferences(LauncherActivity.PREFS_NAME, Context.MODE_PRIVATE);
        long lastCheck = prefs.getLong(KEY_LAST_CHECK_MS, 0L);
        String cached = prefs.getString(KEY_CACHED_MANIFEST, null);
        String cachedSource = prefs.getString(KEY_CACHED_SOURCE, "");

        if (!force && cached != null && (System.currentTimeMillis() - lastCheck) < CACHE_TTL_MS) {
            try {
                UpdateInfo info = UpdateInfo.parse(cached, cachedSource);
                Log.i(TAG, "Using cached update manifest (" + cachedSource + ")");
                return info;
            } catch (Exception ignored) {
                // Broken cache entry - refetch below.
            }
        }

        Exception lastError = null;
        for (String url : MANIFEST_URLS) {
            try {
                UpdateInfo info = UpdateInfo.parse(httpGet(url), url);
                if (!info.hasApkUrl()) {
                    // No explicit link - use the APK asset of the newest GitHub release.
                    info = info.withApkUrl(resolveApkUrlFromGithub());
                }
                prefs.edit()
                        .putLong(KEY_LAST_CHECK_MS, System.currentTimeMillis())
                        .putString(KEY_CACHED_MANIFEST, info.toJson())
                        .putString(KEY_CACHED_SOURCE, url)
                        .apply();
                Log.i(TAG, "Loaded update manifest from " + url + ": " + info);
                return info;
            } catch (Exception e) {
                lastError = e;
                Log.w(TAG, "Cannot load update manifest from " + url + ": " + describe(e));
            }
        }
        if (lastError != null) {
            throw lastError;
        }
        return null;
    }

    /** Picks the first {@code .apk} asset of the newest GitHub release. */
    public static String resolveApkUrlFromGithub() throws Exception {
        JSONObject release = new JSONObject(httpGet(GITHUB_LATEST_RELEASE_URL));
        JSONArray assets = release.optJSONArray("assets");
        if (assets != null) {
            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.optJSONObject(i);
                if (asset == null) continue;
                String name = asset.optString("name", "");
                String url = asset.optString("browser_download_url", null);
                if (name.toLowerCase(Locale.US).endsWith(".apk") && url != null && !url.isEmpty()) {
                    return url;
                }
            }
        }
        throw new IOException("no APK asset in the latest GitHub release");
    }

    // ---------------------------------------------------------------------------------------------
    // Downloading & installing
    // ---------------------------------------------------------------------------------------------

    /** Downloads the APK (streamed to a .part file, renamed on success). Runs on its own thread. */
    public static void downloadApk(final Context context, final UpdateInfo info,
                                   final DownloadListener listener) {
        final Context app = context.getApplicationContext();
        Thread worker = new Thread(() -> {
            HttpURLConnection conn = null;
            InputStream in = null;
            OutputStream out = null;
            File part = null;
            try {
                if (!info.hasApkUrl()) {
                    throw new IOException("update manifest has no APK download link");
                }
                File dir = getUpdateDir(app);
                File target = new File(dir, apkFileName(info));
                part = new File(dir, target.getName() + ".part");
                if (part.exists() && !part.delete()) {
                    Log.w(TAG, "Cannot remove stale partial download " + part);
                }

                conn = (HttpURLConnection) new URL(info.apkUrl).openConnection();
                conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
                conn.setReadTimeout(READ_TIMEOUT_MS);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("User-Agent", USER_AGENT);
                conn.connect();

                int code = conn.getResponseCode();
                if (code != HttpURLConnection.HTTP_OK) {
                    throw new IOException("HTTP " + code + " " + conn.getResponseMessage());
                }

                long total = conn.getContentLength();
                if (total <= 0) {
                    total = info.sizeBytes;
                }

                in = conn.getInputStream();
                out = new FileOutputStream(part);
                byte[] buffer = new byte[16384];
                long done = 0;
                int read;
                int lastPercent = -1;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    done += read;
                    if (total > 0) {
                        int percent = (int) (done * 100 / total);
                        if (percent != lastPercent) {
                            lastPercent = percent;
                            listener.onProgress(percent, done, total);
                        }
                    }
                }
                out.flush();
                out.close();
                out = null;

                if (total > 0 && done != total) {
                    throw new IOException("incomplete download (" + done + " of " + total + " bytes)");
                }
                if (done < 4096) {
                    throw new IOException("downloaded file is too small (" + done + " bytes)");
                }
                if (target.exists() && !target.delete()) {
                    Log.w(TAG, "Cannot replace old APK " + target);
                }
                if (!part.renameTo(target)) {
                    throw new IOException("cannot save APK to " + target.getAbsolutePath());
                }
                part = null;
                Log.i(TAG, "APK downloaded: " + target + " (" + done + " bytes)");
                listener.onSuccess(target);
            } catch (Exception e) {
                Log.w(TAG, "APK download failed", e);
                listener.onError(e);
            } finally {
                closeQuietly(out);
                closeQuietly(in);
                if (conn != null) conn.disconnect();
                if (part != null && part.exists() && !part.delete()) {
                    Log.w(TAG, "Cannot remove partial download " + part);
                }
            }
        }, "NearChuckle-UpdateDownload");
        worker.setDaemon(true);
        worker.start();
    }

    /** Directory for downloaded APKs (app specific, needs no storage permission). */
    public static File getUpdateDir(Context context) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (dir == null) {
            dir = new File(context.getFilesDir(), "updates");
        }
        if (!dir.exists() && !dir.mkdirs()) {
            Log.w(TAG, "Cannot create update directory " + dir);
        }
        return dir;
    }

    private static String apkFileName(UpdateInfo info) {
        String base = info.versionName != null ? info.versionName : String.valueOf(info.versionCode);
        StringBuilder sb = new StringBuilder("NearChuckle-");
        for (int i = 0; i < base.length(); i++) {
            char c = base.charAt(i);
            sb.append(Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == '_' ? c : '_');
        }
        return sb.append(".apk").toString();
    }

    /** Android 8+ needs the "install unknown apps" permission before the installer can start. */
    public static boolean canInstallPackages(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return true;
        }
        try {
            return context.getPackageManager().canRequestPackageInstalls();
        } catch (Exception e) {
            return false;
        }
    }

    /** Settings screen where the user allows this app to install APKs. */
    public static Intent unknownSourcesSettingsIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /** Hands the downloaded APK over to the system package installer. */
    public static void installApk(Activity activity, File apk) throws IOException {
        if (apk == null || !apk.exists()) {
            throw new IOException("APK file is missing");
        }
        Uri uri = FileProvider.getUriForFile(activity,
                activity.getPackageName() + FILE_PROVIDER_SUFFIX, apk);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, APK_MIME);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            throw new IOException("no app can install APK files: " + describe(e), e);
        }
    }

    /** Deletes previously downloaded update files (called after a successful install). */
    public static void clearDownloadedUpdates(Context context) {
        try {
            File[] files = getUpdateDir(context).listFiles();
            if (files == null) return;
            for (File f : files) {
                if (f.isFile() && f.getName().toLowerCase(Locale.US).endsWith(".apk")) {
                    boolean deleted = f.delete();
                    Log.i(TAG, "Removed old update file " + f + " (deleted=" + deleted + ")");
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Cannot clear downloaded updates", e);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Small utilities
    // ---------------------------------------------------------------------------------------------

    /** Simple blocking GET that returns the body as text. */
    public static String httpGet(String urlString) throws IOException {
        HttpURLConnection conn = null;
        InputStream in = null;
        BufferedReader reader = null;
        try {
            conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept", "application/json, text/plain, */*");
            conn.connect();

            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + code + " for " + urlString);
            }
            in = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } finally {
            closeQuietly(reader);
            closeQuietly(in);
            if (conn != null) conn.disconnect();
        }
    }

    public static String formatSize(long bytes) {
        if (bytes <= 0) return "?";
        double mb = bytes / (1024.0 * 1024.0);
        if (mb >= 1.0) {
            return String.format(Locale.US, "%.1f MB", mb);
        }
        return String.format(Locale.US, "%.0f KB", bytes / 1024.0);
    }

    static String describe(Throwable t) {
        String msg = t.getMessage();
        if (msg == null || msg.trim().isEmpty()) {
            return t.getClass().getSimpleName();
        }
        return t.getClass().getSimpleName() + ": " + msg;
    }

    private static void closeQuietly(@Nullable java.io.Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Exception ignored) {
        }
    }

    /** Unused here but handy for callers that want to check the permission state themselves. */
    @SuppressWarnings("unused")
    private static boolean hasPermission(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
}
