@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.loukwn.stagestepbarexample"
        minSdk = libs.versions.minSdkView.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }

    namespace = "com.loukwn.stagestepbar_example"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaTarget.get())

        kotlinOptions {
            jvmTarget = libs.versions.javaTarget.get()
        }
    }
}

dependencies {
    implementation(project(":stagestepbar"))

    implementation(libs.android.core)
    implementation(libs.android.appcompat)
    implementation(libs.android.activity)
    implementation(libs.android.material)
    implementation(libs.kotlin.coroutines)
}
