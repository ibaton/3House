# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/ibaton/ide/android-studio-08/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-dontobfuscate

-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*


#picasso
-dontwarn com.squareup.okhttp.**

# OKIO
-dontwarn okio.**

# Retrofit
-dontwarn retrofit2.Platform$Java8

# Dagger
-dontwarn com.google.errorprone.annotations.**
-keep class com.google.gson.** { *; }
-keep class com.google.inject.* { *; }

# slf4j logger
-dontwarn javax.naming.**
-dontwarn javax.servlet.**
-dontwarn org.slf4j.**

-keep class org.apache.http.* { *; }

# Butterknife
-keep class **$$ViewBinder { *; }

# greenrobot:eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Realm
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class * { *; }
-dontwarn javax.**
-dontwarn io.realm.**

-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient

-dontwarn java.lang.invoke.*

# About library
-keep class .R
-keep class **.R$* {
    <fields>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep the BuildConfig
-keep class com.example.BuildConfig { *; }

# Keep the support library
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

# Keep the support library
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

# Keep the support library
-keep class se.treehou.ng.ohcommunicator.** { *; }
-keep interface se.treehou.ng.ohcommunicator.** { *; }

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
