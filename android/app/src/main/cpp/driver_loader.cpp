#include <jni.h>
#include <string>
#include <android/log.h>
#include <fcntl.h>
#include <unistd.h>
#include <dlfcn.h>
#include <sys/stat.h>

#define TAG "NearChuckle-DriverLoader"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

#if defined(__aarch64__)
#include <adrenotools/driver.h>
#define ADRENOTOOLS_SUPPORTED 1
#else
#define ADRENOTOOLS_SUPPORTED 0
#endif

static void *g_customVulkanHandle = nullptr;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_nearchuckle_farcry_driver_DriverHook_nativeIsAdreno(JNIEnv *env, jclass clazz) {
    // Check if Qualcomm /dev/kgsl-3d0 device exists
    if (access("/dev/kgsl-3d0", F_OK) == 0) {
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_nearchuckle_farcry_driver_DriverHook_nativeCheckSupport(JNIEnv *env, jclass clazz) {
#if ADRENOTOOLS_SUPPORTED
    return JNI_TRUE;
#else
    return JNI_FALSE;
#endif
}

JNIEXPORT jboolean JNICALL
Java_com_nearchuckle_farcry_driver_DriverHook_nativeInitDriver(
        JNIEnv *env, jclass clazz,
        jstring hookLibDirStr,
        jstring customDriverDirStr,
        jstring customDriverNameStr,
        jboolean enableTurbo) {

    const char *hookLibDir = hookLibDirStr ? env->GetStringUTFChars(hookLibDirStr, nullptr) : nullptr;
    const char *customDriverDir = customDriverDirStr ? env->GetStringUTFChars(customDriverDirStr, nullptr) : nullptr;
    const char *customDriverName = customDriverNameStr ? env->GetStringUTFChars(customDriverNameStr, nullptr) : nullptr;

    LOGI("Initializing GPU Driver: customDriverDir=%s, customDriverName=%s, turbo=%d",
         customDriverDir ? customDriverDir : "(null)",
         customDriverName ? customDriverName : "(null)",
         (int)enableTurbo);

#if ADRENOTOOLS_SUPPORTED
    if (enableTurbo) {
        LOGI("Enabling GPU Turbo mode via adrenotools...");
        adrenotools_set_turbo(true);
    }

    if (customDriverDir && customDriverName && strlen(customDriverName) > 0) {
        std::string fullDriverPath = std::string(customDriverDir);
        if (!fullDriverPath.empty() && fullDriverPath.back() != '/') {
            fullDriverPath += '/';
        }
        fullDriverPath += customDriverName;

        struct stat st{};
        if (stat(fullDriverPath.c_str(), &st) != 0) {
            LOGE("Custom driver file does not exist: %s", fullDriverPath.c_str());
            if (hookLibDir) env->ReleaseStringUTFChars(hookLibDirStr, hookLibDir);
            if (customDriverDir) env->ReleaseStringUTFChars(customDriverDirStr, customDriverDir);
            if (customDriverName) env->ReleaseStringUTFChars(customDriverNameStr, customDriverName);
            return JNI_FALSE;
        }

        LOGI("Found custom Turnip driver at %s, hooking with adrenotools...", fullDriverPath.c_str());

        int featureFlags = ADRENOTOOLS_DRIVER_CUSTOM;
        g_customVulkanHandle = adrenotools_open_libvulkan(
                RTLD_NOW,
                featureFlags,
                nullptr, // tmpLibDir
                hookLibDir,
                customDriverDir,
                customDriverName,
                nullptr, // fileRedirectDir
                nullptr  // userMappingHandle
        );

        if (g_customVulkanHandle) {
            LOGI("adrenotools successfully loaded custom Turnip driver!");
        } else {
            LOGW("adrenotools_open_libvulkan returned null, falling back to direct ICD / dlopen: %s", fullDriverPath.c_str());
            void *directHandle = dlopen(fullDriverPath.c_str(), RTLD_NOW | RTLD_GLOBAL);
            if (directHandle) {
                LOGI("Direct dlopen of Turnip driver succeeded!");
                g_customVulkanHandle = directHandle;
            } else {
                LOGE("Direct dlopen failed: %s", dlerror());
            }
        }
    } else {
        LOGI("Using system Vulkan driver.");
    }
#else
    LOGI("adrenotools is not supported on this architecture (non-aarch64).");
    if (customDriverDir && customDriverName) {
        std::string fullPath = std::string(customDriverDir) + "/" + customDriverName;
        dlopen(fullPath.c_str(), RTLD_NOW | RTLD_GLOBAL);
    }
#endif

    if (hookLibDir) env->ReleaseStringUTFChars(hookLibDirStr, hookLibDir);
    if (customDriverDir) env->ReleaseStringUTFChars(customDriverDirStr, customDriverDir);
    if (customDriverName) env->ReleaseStringUTFChars(customDriverNameStr, customDriverName);

    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_com_nearchuckle_farcry_driver_DriverHook_nativeSetTurbo(JNIEnv *env, jclass clazz, jboolean turbo) {
#if ADRENOTOOLS_SUPPORTED
    adrenotools_set_turbo(turbo == JNI_TRUE);
    LOGI("GPU Turbo set to: %d", (int)turbo);
#endif
}

} // extern "C"
