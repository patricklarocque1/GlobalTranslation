# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Hilt ProGuard rules
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
-keep class * extends dagger.Module
-keep @dagger.hilt.InstallIn class *
-keep @javax.inject.Inject class *
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}
-keepclassmembers class * {
    @javax.inject.Inject <fields>;
}

# Keep the Application class (fixed package name)
-keep class com.example.globaltranslation.GloabTranslationApplication { *; }

# ML Kit keep rules
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Keep ML Kit model classes
-keep class com.google.mlkit.nl.translate.** { *; }
-keep class com.google.mlkit.vision.text.** { *; }
-keep class com.google.mlkit.common.** { *; }

# Room keep rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# Keep Room generated classes
-keep class com.example.globaltranslation.data.local.** { *; }

# Kotlin coroutines keep rules
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# DataStore
-keep class androidx.datastore.*.** { *; }

# Keep data classes used in StateFlow
-keep class com.example.globaltranslation.ui.**.* { *; }
-keep class com.example.globaltranslation.core.model.** { *; }
-keep class com.example.globaltranslation.core.provider.** { *; }

# OkHttp and Conscrypt (used by Google Play Services for ML Kit downloads)
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Suppress warnings about accessing hidden APIs (OkHttp SSL optimizations)
# These are harmless - OkHttp gracefully falls back when reflection is denied
-dontwarn com.android.org.conscrypt.**
-keep class com.android.org.conscrypt.** { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile