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