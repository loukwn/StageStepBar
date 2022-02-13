plugins {
    id("com.android.library")
    id("kotlin-android")
    id("app.cash.paparazzi")
}

android {
    compileSdk = 29
    defaultConfig {
        minSdk = 25
    }
}

dependencies {
    implementation(project(Config.Modules.lib))
    testImplementation(project(Config.Modules.lib))
}
