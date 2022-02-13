@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = Config.Project.compileSdk
    buildToolsVersion = Config.Project.buildToolsVersion

    defaultConfig {
        applicationId = "com.loukwn.stagestepbarexample"
        minSdk = Config.Project.minSdkView
        targetSdk = Config.Project.targetSdk
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(Config.Modules.lib))

    implementation(Config.Libs.Android.coreKtx)
    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.material)
}
