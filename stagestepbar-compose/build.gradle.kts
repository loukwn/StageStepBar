plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    compileSdk = Config.Project.compileSdk
    buildToolsVersion = Config.Project.buildToolsVersion
    namespace = "com.loukwn.stagestepbar_compose"

    defaultConfig {
        minSdk = Config.Project.minSdkCompose
        targetSdk = Config.Project.targetSdk

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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileOptions {
        kotlinOptions {
            if (project.findProperty("stagestepbar_compose.enableComposeCompilerReports") == "true") {
                freeCompilerArgs = freeCompilerArgs
                    .plus("-P")
                    .plus(
                        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                                project.buildDir.absolutePath + "/compose_metrics"
                    )
                    .plus("-P")
                    .plus(
                        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                                project.buildDir.absolutePath + "/compose_metrics"
                    )
            }
        }
    }
}

dependencies {
    implementation(Config.Libs.Android.coreKtx)
    implementation(Config.Libs.Android.Compose.ui)
    implementation(Config.Libs.Android.Compose.material)
    implementation(Config.Libs.Android.Compose.uiToolingPreview)
    debugImplementation(Config.Libs.Android.Compose.uiTooling)
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
