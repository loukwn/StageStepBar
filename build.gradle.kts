// Lists all plugins used throughout the project without applying them.
plugins {
    id("com.android.application") version libs.versions.agp apply false
    id("com.android.library") version libs.versions.agp apply false
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin.version apply false
    id("org.jlleitschuh.gradle.ktlint") version libs.versions.ktlint apply false
    id("app.cash.paparazzi") version libs.versions.paparazzi apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
