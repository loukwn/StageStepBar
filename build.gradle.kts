buildscript {
    repositories {
        google()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath(Config.BuildPlugins.buildGradlePlugin)
        classpath(Config.BuildPlugins.kotlinGradlePlugin)
        classpath(Config.BuildPlugins.ktlintGradlePlugin)
        classpath(Config.BuildPlugins.paparazziPlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
