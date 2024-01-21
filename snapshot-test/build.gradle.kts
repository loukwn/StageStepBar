plugins {
    id("com.android.library")
    id("kotlin-android")
    id("app.cash.paparazzi")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdkSnapshotTesting.get().toInt()
    }

    namespace = "com.loukwn.snapshot_test"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.version.get()
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
    testImplementation(project(":stagestepbar"))
    testImplementation(project(":stagestepbar-compose"))

    testImplementation(libs.compose.ui)
    testImplementation(libs.compose.material)
}

apply(from = "guava-fix.gradle.kts")
