# 1. Preserve line numbers and source file names for better crash logs
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 2. Firestore Rules: Prevents the BeanMapper / CustomClassMapper crash
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.firebase.firestore.** { *; }

# 3. Cloudinary Rules: Ensures the MediaManager and UploadCallbacks work
-keep class com.cloudinary.** { *; }
-keep interface com.cloudinary.** { *; }

# 4. Glide Rules: Prevents "Cannot resolve symbol" issues in production
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.** { *; }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# 5. Your Project Models: CRITICAL to prevent mapping errors
# This ensures Firestore can find your MemberModel and UserModel fields
-keep class com.agpitcodeclub.codeclubagpit.** { *; }
-keepclassmembers class com.agpitcodeclub.codeclubagpit.** {
    public <init>(...);
    public *** get*();
    public void set*(***);
}

# 6. Fix for "Missing Classes" from OkHttp & Cloudinary
-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn com.cloudinary.android.MediaManager

# 7. SoLoader (Native library loading)
# Necessary for the 16 KB memory alignment support
-keep class com.facebook.soloader.** { *; }
-dontwarn com.facebook.soloader.**

# 8. Volley (if R8 flags it)
-keep class com.android.volley.** { *; }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.squareup.picasso.Callback
-dontwarn com.squareup.picasso.Picasso$Builder
-dontwarn com.squareup.picasso.Picasso
-dontwarn com.squareup.picasso.RequestCreator