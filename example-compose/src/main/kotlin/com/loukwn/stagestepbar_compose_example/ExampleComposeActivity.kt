package com.loukwn.stagestepbar_compose_example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.lifecycle.lifecycleScope
import com.loukwn.stagestepbar_compose_example.presentation.ExampleComposeUi
import com.loukwn.stagestepbar_compose_example.ui.theme.StageStepBarTheme

@ExperimentalUnitApi
internal class ExampleComposeActivity : ComponentActivity() {

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