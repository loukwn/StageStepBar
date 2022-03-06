package com.loukwn.example_compose.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.loukwn.example_compose.UiModel

@ExperimentalUnitApi
@Composable
internal fun StateListItem(
    uiModel: UiModel,
    onNullToggleChanged: (Boolean) -> Unit,
    onStageChanged: (Int) -> Unit,
    onStepChanged: (Int) -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Current State",
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontWeight = FontWeight.ExtraBold
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                onNullToggleChanged(uiModel.stageStepBarConfig.currentState != null)
            }) {
                Text(
                    modifier = Modifier.padding(top = 18.dp, end = 4.dp),
                    text = "null?",
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontWeight = FontWeight.ExtraBold
                )
                Checkbox(
                    modifier = Modifier.padding(top = 16.dp),
                    checked = uiModel.stageStepBarConfig.currentState == null,
                    onCheckedChange = {
                        onNullToggleChanged(it)
                    }
                )
            }
        }

        Row {
            var stageText by rememberSaveable { mutableStateOf(uiModel.currentStage.toString()) }
            var stepText by rememberSaveable { mutableStateOf(uiModel.currentStep.toString()) }

            TextField(
                value = stageText,
                enabled = uiModel.stageStepBarConfig.currentState != null,
                label = { Text("Stage") },
                onValueChange = { value ->
                    stageText = value
                    try {
                        val stage = value.toInt()
                        if (stage >= 0) {
                            onStageChanged(stage)
                        }
                    } catch (e: NumberFormatException) {
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                ),
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .weight(1f),

                )
            TextField(
                value = stepText,
                label = { Text("Step") },
                enabled = uiModel.stageStepBarConfig.currentState != null,
                onValueChange = { value ->
                    stepText = value
                    try {
                        val step = value.toInt()
                        if (step >= 0) {
                            onStepChanged(step)
                        }
                    } catch (e: NumberFormatException) {
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                ),
                modifier = Modifier
                    .padding(top = 8.dp, start = 4.dp)
                    .weight(1f),
            )
        }
    }
}

@ExperimentalUnitApi
@Preview
@Composable
private fun Preview() {
    StateListItem(
        UiModel.default(),
        onNullToggleChanged = {},
        onStageChanged = {},
        onStepChanged = {})
}