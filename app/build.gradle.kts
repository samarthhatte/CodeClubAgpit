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
        versionCode = 30
        versionName = "4.0"
        ndkVersion = "28.0.12433510"
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }

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
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    implementation(libs.okhttp)
    implementation(libs.circleimageview)
    implementation(libs.volley)
    implementation(libs.photoview)

    implementation(libs.glide)
    implementation(libs.ext.junit)
    implementation(libs.monitor)
    annotationProcessor(libs.compiler)
    testImplementation(libs.junit)
    implementation(libs.viewpager2)
    androidTestImplementation(libs.junit)
    implementation(libs.cloudinary.android)
    implementation(libs.soloader)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
}