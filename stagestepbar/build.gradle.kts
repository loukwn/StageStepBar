import com.android.build.gradle.ProguardFiles.getDefaultProguardFile

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdkView.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    namespace = "com.loukwn.stagestepbar"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
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
    implementation(libs.android.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.findByName("release"))

                group = "com.loukwn"
                version = libs.versions.viewLibraryRelease.get()
            }
        }
    }
}
