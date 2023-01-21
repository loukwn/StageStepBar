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
}

dependencies {
    implementation(project(Config.Modules.lib))
    testImplementation(project(Config.Modules.lib))
}
