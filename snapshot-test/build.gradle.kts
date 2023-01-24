plugins {
    id("com.android.library")
    id("kotlin-android")
    id("app.cash.paparazzi")
}

android {
    namespace = "com.loukwn.snapshot_test"
    compileSdk = 33
    defaultConfig {
        minSdk = 25
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Config.Version.compose
    }
}

dependencies {
    implementation(project(Config.Modules.lib))
    testImplementation(project(Config.Modules.lib))
    testImplementation(project(Config.Modules.libCompose))
    testImplementation(Config.Libs.Android.Compose.ui)
    testImplementation(Config.Libs.Android.Compose.material)
}
