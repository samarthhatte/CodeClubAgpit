plugins {
    alias(libs.plugins.android.application)
//    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.agpitcodeclub.codeclubagpit"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.agpitcodeclub.codeclubagpit"
        minSdk = 24
        targetSdk = 35
        versionCode = 24
        versionName = "3.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // ADD THIS SECTION TO SUPPORT 16 KB PAGE SIZES
    packaging {
        jniLibs {
            // This ensures native libraries are extracted and aligned correctly
            useLegacyPackaging = false
        }
    }

    buildTypes {
        release {
            ndk.debugSymbolLevel = "full"
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.ext.junit)
    implementation(libs.monitor)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("junit:junit:4.13.2")
    implementation("com.cloudinary:cloudinary-android:3.0.2")
    implementation("com.facebook.soloader:soloader:0.12.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
}
