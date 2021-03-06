plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    compileSdk = Config.Project.compileSdk
    buildToolsVersion = Config.Project.buildToolsVersion

    defaultConfig {
        minSdk = Config.Project.minSdkView
        targetSdk = Config.Project.targetSdk
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(Config.Libs.Android.coreKtx)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.findByName("release"))

                group = "com.loukwn"
                version = Config.Project.libraryReleaseVersion
            }
        }
    }
}
