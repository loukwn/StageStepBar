plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = Config.Project.compileSdk
    buildToolsVersion = Config.Project.buildToolsVersion

    defaultConfig {
        minSdk = Config.Project.minSdkCompose
        targetSdk = Config.Project.targetSdk
        applicationId = "com.loukwn.example_compose"
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Config.Version.compose
    }
}

dependencies {
    implementation(project(Config.Modules.libCompose))

    implementation(Config.Libs.Android.coreKtx)
    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.material)
    implementation(Config.Libs.Android.Compose.ui)
    implementation(Config.Libs.Android.Compose.material)
    implementation(Config.Libs.Android.Compose.uiToolingPreview)
    implementation(Config.Libs.Android.lifecycleRuntimeKtx)
    implementation(Config.Libs.Android.Compose.activity)
    debugImplementation(Config.Libs.Android.Compose.uiTooling)
}
