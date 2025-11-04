# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Aggressive optimizations for smaller APK
-optimizationpasses 5
-overloadaggressively
-repackageclasses ''
-allowaccessmodification

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Keep only essential Compose runtime (not all classes)
-keep class androidx.compose.runtime.Composer { *; }
-keep class androidx.compose.runtime.Recomposer { *; }
-keep class androidx.compose.runtime.CompositionLocal { *; }

# Keep Material 3 theme and colors (not all classes)
-keep class androidx.compose.material3.ColorScheme { *; }
-keep class androidx.compose.material3.Typography { *; }
-keep,allowobfuscation class androidx.compose.material3.** { *; }

# Keep Material components used (not all)
-keep class com.google.android.material.color.** { *; }
-keepclassmembers class com.google.android.material.** {
    public <init>(...);
}

# Aggressively remove unused code
-dontwarn **
-ignorewarnings

# Remove unused resources at build time
-printusage usage.txt
-printmapping mapping.txt

# Keep only app activities and data classes
-keep class com.halfminute.itmadness.**Activity { *; }
-keep class com.halfminute.itmadness.data.** { *; }
-keep class com.halfminute.itmadness.utils.** { *; }
-keepclassmembers class com.halfminute.itmadness.** {
    public <init>(...);
}

# Optimize Kotlin code
-dontwarn kotlin.**
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
    public static void throw*(...);
}

# Strip R8 metadata
-repackageclasses 'a'