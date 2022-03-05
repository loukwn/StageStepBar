package com.loukwn.example_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.lifecycle.lifecycleScope
import com.loukwn.example_compose.presentation.ExampleComposeUi
import com.loukwn.example_compose.ui.theme.StageStepBarTheme
import kotlinx.coroutines.flow.collect

@ExperimentalUnitApi
class ExampleComposeActivity : ComponentActivity() {

    private val viewModel: ViewModelContract by viewModels<ExampleComposeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect {
                when (it) {
                    Event.Close -> finish()
                    null -> {}
                }
            }
        }

        setContent {
            StageStepBarTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val uiModel = viewModel.uiModels.collectAsState().value
                    ExampleComposeUi(uiModel, viewModel)
                }
            }
        }
    }
}