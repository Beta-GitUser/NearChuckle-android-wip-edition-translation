#!/bin/bash
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_DIR="$( cd "$DIR/.." && pwd )"

ARCH="arm64"
BUILD_TYPE="Release"
NDK_PATH="${ANDROID_NDK_HOME:-${ANDROID_NDK_ROOT:-$NDK_HOME}}"

usage() {
    echo "Usage: ./build_android.sh [--arch arm64|arm|x86_64] [--debug|--release] [--ndk /path/to/ndk]"
    echo "  --arch: target architecture (default: arm64)"
    echo "  --ndk: path to Android NDK"
    echo "  --debug: build debug binaries"
    echo "  --release: build release binaries (default)"
    exit 0
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --help|-h)
            usage
            ;;
        --arch)
            ARCH="$2"
            shift 2
            ;;
        --ndk)
            NDK_PATH="$2"
            shift 2
            ;;
        --debug)
            BUILD_TYPE="Debug"
            shift
            ;;
        --release)
            BUILD_TYPE="Release"
            shift
            ;;
        *)
            echo "Unknown argument: $1"
            exit 1
            ;;
    esac
done

case "$ARCH" in
    arm64|aarch64)
        ABI="arm64-v8a"
        ;;
    arm|armeabi-v7a)
        ABI="armeabi-v7a"
        ;;
    x86_64)
        ABI="x86_64"
        ;;
    *)
        echo "Unsupported architecture: $ARCH (choose arm64, arm, or x86_64)"
        exit 1
        ;;
esac

if [ -z "$NDK_PATH" ] || [ ! -d "$NDK_PATH" ]; then
    # Try finding NDK in common locations
    COMMON_LOCATIONS=(
        "$HOME/Android/Sdk/ndk/"*
        "$HOME/Android/Sdk/ndk-bundle"
        "/opt/android-ndk"*
        "/opt/android-sdk/ndk/"*
    )
    for loc in "${COMMON_LOCATIONS[@]}"; do
        if [ -d "$loc" ]; then
            NDK_PATH="$loc"
            break
        fi
    done
fi

if [ -z "$NDK_PATH" ] || [ ! -d "$NDK_PATH" ]; then
    echo "================================================================="
    echo "WARNING: Android NDK not found in environment."
    echo "Please set ANDROID_NDK_HOME or specify --ndk /path/to/android-ndk"
    echo "================================================================="
    exit 1
fi

TOOLCHAIN_FILE="$NDK_PATH/build/cmake/android.toolchain.cmake"
if [ ! -f "$TOOLCHAIN_FILE" ]; then
    echo "Error: Android CMake toolchain file not found at: $TOOLCHAIN_FILE"
    exit 1
fi

echo "================================================================="
echo "Building NearChuckle (Far Cry) for Android"
echo "  ABI:        $ABI"
echo "  Build Type: $BUILD_TYPE"
echo "  NDK Path:   $NDK_PATH"
echo "================================================================="

BUILD_DIR="$ROOT_DIR/build_android/$ABI"
mkdir -p "$BUILD_DIR"

cmake -B "$BUILD_DIR" -S "$ROOT_DIR" \
    -DCMAKE_TOOLCHAIN_FILE="$TOOLCHAIN_FILE" \
    -DANDROID_ABI="$ABI" \
    -DANDROID_PLATFORM=android-24 \
    -DANDROID_STL=c++_shared \
    -DCMAKE_BUILD_TYPE="$BUILD_TYPE" \
    -DDISABLE_CG=ON \
    -DCMAKE_EXPORT_COMPILE_COMMANDS=ON

NPROC=$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4)
cmake --build "$BUILD_DIR" -- -j"$NPROC"

JNI_LIBS_DIR="$ROOT_DIR/android/app/src/main/jniLibs/$ABI"
mkdir -p "$JNI_LIBS_DIR"

echo "Deploying shared libraries to $JNI_LIBS_DIR..."

# Copy libc++_shared
find "$NDK_PATH" -name "libc++_shared.so" | grep "$ABI" | head -n 1 | while read -r lib; do
    cp "$lib" "$JNI_LIBS_DIR/"
done

# Copy built NearChuckle and CryEngine libraries
find "$BUILD_DIR" -name "*.so" -exec cp {} "$JNI_LIBS_DIR/" \;

# Strip binaries if release
STRIP_TOOL=$(find "$NDK_PATH" -name "llvm-strip" | head -n 1)
if [ "$BUILD_TYPE" = "Release" ] && [ -n "$STRIP_TOOL" ] && [ -x "$STRIP_TOOL" ]; then
    echo "Stripping shared libraries..."
    "$STRIP_TOOL" "$JNI_LIBS_DIR"/*.so 2>/dev/null || true
fi

echo "================================================================="
echo "Successfully built and deployed Far Cry libraries for $ABI!"
echo "Destination: $JNI_LIBS_DIR"
echo "================================================================="
