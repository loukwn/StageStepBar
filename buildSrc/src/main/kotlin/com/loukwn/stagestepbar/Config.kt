@file:Suppress("PackageDirectoryMismatch")

object Config {

    object Version {
        const val compose = "1.0.0"
    }

    object BuildPlugins {
        const val buildGradlePlugin = "com.android.tools.build:gradle:_"
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:_"
        const val ktlintGradlePlugin = "org.jlleitschuh.gradle:ktlint-gradle:_"
        const val paparazziPlugin = "app.cash.paparazzi:paparazzi-gradle-plugin:_"
    }

    object Libs {

        object Android {
            const val appcompat = "androidx.appcompat:appcompat:_"
            const val coreKtx = "androidx.core:core-ktx:_"
            const val material = "com.google.android.material:material:_"
            const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:_"

            object Compose {
                const val ui = "androidx.compose.ui:ui:_"
                const val material = "androidx.compose.material:material:_"
                const val uiTooling = "androidx.compose.ui:ui-tooling:_"
                const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview:_"
                const val activity = "androidx.activity:activity-compose:_"
            }
        }
    }

    object Modules {
        const val lib = ":stagestepbar"
        const val libCompose = ":stagestepbar-compose"
    }
}
