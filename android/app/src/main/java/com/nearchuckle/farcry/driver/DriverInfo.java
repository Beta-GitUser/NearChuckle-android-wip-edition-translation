package com.nearchuckle.farcry.driver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Represents an installed GPU driver (System driver or Turnip Adrenotools custom driver).
 */
public class DriverInfo implements Serializable {
    public static final String DRIVER_SYSTEM_ID = "system_default";

    private String id;
    private String name;
    private String description;
    private String author;
    private String vendor;
    private String driverVersion;
    private String packageVersion;
    private int minApi;
    private String libraryName;
    private String installedPath; // Path where files are extracted

    public DriverInfo() {
    }

    public static DriverInfo createSystemDriver() {
        DriverInfo info = new DriverInfo();
        info.id = DRIVER_SYSTEM_ID;
        boolean isRu = java.util.Locale.getDefault().getLanguage().equals("ru");
        info.name = isRu ? "Системный Vulkan драйвер" : "System Vulkan Driver";
        info.description = isRu ? "Встроенный драйвер устройства (Qualcomm / Mali / PowerVR)" : "Built-in device driver (Qualcomm / Mali / PowerVR)";
        info.author = "System";
        info.vendor = "System";
        info.driverVersion = "Default";
        info.packageVersion = "1.0";
        info.minApi = 21;
        info.libraryName = "libvulkan.so";
        info.installedPath = "";
        return info;
    }

    public boolean isSystem() {
        return DRIVER_SYSTEM_ID.equals(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getDriverVersion() {
        return driverVersion;
    }

    public void setDriverVersion(String driverVersion) {
        this.driverVersion = driverVersion;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public int getMinApi() {
        return minApi;
    }

    public void setMinApi(int minApi) {
        this.minApi = minApi;
    }

    public String getLibraryName() {
        return libraryName != null && !libraryName.isEmpty() ? libraryName : "libvulkan_freedreno.so";
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getInstalledPath() {
        return installedPath;
    }

    public void setInstalledPath(String installedPath) {
        this.installedPath = installedPath;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("description", description);
        json.put("author", author);
        json.put("vendor", vendor);
        json.put("driverVersion", driverVersion);
        json.put("packageVersion", packageVersion);
        json.put("minApi", minApi);
        json.put("libraryName", libraryName);
        json.put("installedPath", installedPath);
        return json;
    }

    public static DriverInfo fromJson(JSONObject json) {
        DriverInfo info = new DriverInfo();
        info.id = json.optString("id", "");
        info.name = json.optString("name", "Turnip Driver");
        info.description = json.optString("description", "");
        info.author = json.optString("author", "Mesa / Freedreno");
        info.vendor = json.optString("vendor", "Mesa");
        info.driverVersion = json.optString("driverVersion", "");
        info.packageVersion = json.optString("packageVersion", "");
        info.minApi = json.optInt("minApi", 26);
        info.libraryName = json.optString("libraryName", "libvulkan_freedreno.so");
        info.installedPath = json.optString("installedPath", "");
        return info;
    }

    @Override
    public String toString() {
        if (isSystem()) {
            return name;
        }
        if (driverVersion != null && !driverVersion.isEmpty()) {
            return name + " (" + driverVersion + ")";
        }
        return name;
    }
}
