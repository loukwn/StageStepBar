plugins {
    id("com.android.library")
    id("kotlin-android")
    id("app.cash.paparazzi")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdkScreenshotTesting.get().toInt()
    }

    namespace = "com.loukwn.snapshot_test"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeVer.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

dependencies {
    implementation(project(":stagestepbar"))
    testImplementation(project(":stagestepbar"))
    testImplementation(project(":stagestepbar-compose"))

    testImplementation(libs.compose.ui)
    testImplementation(libs.compose.material)
}
