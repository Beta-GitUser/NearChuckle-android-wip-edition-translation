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

#include <signal.h>
#include <ucontext.h>

static void *g_customVulkanHandle = nullptr;
static struct sigaction g_old_sigsegv;
static struct sigaction g_old_sigabrt;
static struct sigaction g_old_sigbus;
static struct sigaction g_old_sigfpe;
static struct sigaction g_old_sigill;

static void nativeCrashSignalHandler(int sig, siginfo_t *info, void *ucontext) {
    const char *sigName = "UNKNOWN";
    if (sig == SIGSEGV) sigName = "SIGSEGV (Segmentation violation)";
    else if (sig == SIGABRT) sigName = "SIGABRT (Abort program)";
    else if (sig == SIGBUS)  sigName = "SIGBUS (Bus error)";
    else if (sig == SIGFPE)  sigName = "SIGFPE (Floating point exception)";
    else if (sig == SIGILL)  sigName = "SIGILL (Illegal instruction)";

    uintptr_t pc = 0;
    uintptr_t lr = 0;
    uintptr_t sp = 0;
#if defined(__aarch64__)
    auto *uc = (ucontext_t *)ucontext;
    if (uc) {
        pc = (uintptr_t)uc->uc_mcontext.pc;
        lr = (uintptr_t)uc->uc_mcontext.regs[30];
        sp = (uintptr_t)uc->uc_mcontext.sp;
    }
#endif

    Dl_info pc_info{}, lr_info{};
    if (pc) dladdr((void*)pc, &pc_info);
    if (lr) dladdr((void*)lr, &lr_info);

    LOGE("=================================================================");
    LOGE("CRITICAL NATIVE CRASH DETECTED: Signal %d (%s)", sig, sigName);
    LOGE("Fault address: %p", info ? info->si_addr : nullptr);
    LOGE("PID / TID:     %d / %d", getpid(), gettid());
    if (pc) {
        LOGE("PC (Instruction): 0x%lx (%s + 0x%lx, symbol: %s)",
             (unsigned long)pc,
             pc_info.dli_fname ? pc_info.dli_fname : "(unknown)",
             pc_info.dli_fbase ? (unsigned long)(pc - (uintptr_t)pc_info.dli_fbase) : 0UL,
             pc_info.dli_sname ? pc_info.dli_sname : "(no symbol)");
    }
    if (lr) {
        LOGE("LR (Return):      0x%lx (%s + 0x%lx, symbol: %s)",
             (unsigned long)lr,
             lr_info.dli_fname ? lr_info.dli_fname : "(unknown)",
             lr_info.dli_fbase ? (unsigned long)(lr - (uintptr_t)lr_info.dli_fbase) : 0UL,
             lr_info.dli_sname ? lr_info.dli_sname : "(no symbol)");
    }
    LOGE("=================================================================");

    const char *paths[] = {
        "/data/data/com.nearchuckle.farcry/files/last_crash.txt",
        "/data/user/0/com.nearchuckle.farcry/files/last_crash.txt"
    };

    for (const char *path : paths) {
        FILE *fp = fopen(path, "w");
        if (fp) {
            fprintf(fp, "================================================================\n");
            fprintf(fp, "FAR CRY ANDROID NATIVE CRASH\n");
            fprintf(fp, "================================================================\n");
            fprintf(fp, "Signal:        %d (%s)\n", sig, sigName);
            fprintf(fp, "Fault Address: %p\n", info ? info->si_addr : nullptr);
            fprintf(fp, "PID / TID:     %d / %d\n", getpid(), gettid());
            if (pc) {
                fprintf(fp, "PC (IP):       0x%lx (%s + 0x%lx, %s)\n",
                        (unsigned long)pc,
                        pc_info.dli_fname ? pc_info.dli_fname : "(unknown)",
                        pc_info.dli_fbase ? (unsigned long)(pc - (uintptr_t)pc_info.dli_fbase) : 0UL,
                        pc_info.dli_sname ? pc_info.dli_sname : "(no symbol)");
            }
            if (lr) {
                fprintf(fp, "LR (Return):   0x%lx (%s + 0x%lx, %s)\n",
                        (unsigned long)lr,
                        lr_info.dli_fname ? lr_info.dli_fname : "(unknown)",
                        lr_info.dli_fbase ? (unsigned long)(lr - (uintptr_t)lr_info.dli_fbase) : 0UL,
                        lr_info.dli_sname ? lr_info.dli_sname : "(no symbol)");
            }
            if (sp) {
                fprintf(fp, "SP (Stack):    0x%lx\n", (unsigned long)sp);
            }
            fprintf(fp, "================================================================\n\n");
            fclose(fp);
            break;
        }
    }

    struct sigaction *old_sa = &g_old_sigsegv;
    if (sig == SIGABRT) old_sa = &g_old_sigabrt;
    else if (sig == SIGBUS) old_sa = &g_old_sigbus;
    else if (sig == SIGFPE) old_sa = &g_old_sigfpe;
    else if (sig == SIGILL) old_sa = &g_old_sigill;

    if (old_sa->sa_sigaction) {
        old_sa->sa_sigaction(sig, info, ucontext);
    } else if (old_sa->sa_handler && old_sa->sa_handler != SIG_DFL && old_sa->sa_handler != SIG_IGN) {
        old_sa->sa_handler(sig);
    } else {
        signal(sig, SIG_DFL);
        raise(sig);
    }
}

static void installNativeCrashHandlers() {
    static bool installed = false;
    if (installed) return;
    installed = true;

    struct sigaction sa{};
    sa.sa_flags = SA_SIGINFO | SA_ONSTACK;
    sa.sa_sigaction = nativeCrashSignalHandler;
    sigemptyset(&sa.sa_mask);

    sigaction(SIGSEGV, &sa, &g_old_sigsegv);
    sigaction(SIGABRT, &sa, &g_old_sigabrt);
    sigaction(SIGBUS, &sa, &g_old_sigbus);
    sigaction(SIGFPE, &sa, &g_old_sigfpe);
    sigaction(SIGILL, &sa, &g_old_sigill);
    LOGI("Installed native crash signal handlers.");
}

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

    installNativeCrashHandlers();

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
