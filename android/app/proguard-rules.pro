# Keep native methods and classes used by SDL and JNI
-keep class org.libsdl.app.** { *; }
-keep class com.nearchuckle.farcry.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
