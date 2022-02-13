package com.loukwn.example_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.loukwn.example_compose.presentation.MainUi
import com.loukwn.example_compose.ui.theme.StageStepBarTheme

@ExperimentalUnitApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StageStepBarTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainUi(
                        onClose = { finish() }
                    )
                }
            }
        }
    }
}