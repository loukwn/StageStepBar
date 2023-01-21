package com.loukwn.stagestepbar_compose_example.presentation.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.loukwn.stagestepbar_compose_example.UiModel

@ExperimentalUnitApi
@Composable
internal fun StepsInStagesListItem(
    uiModel: UiModel,
    onChanged: (List<Int>) -> Unit
) {
    Column {
        Text(
            "Steps in Stages config",
            fontSize = TextUnit(18f, TextUnitType.Sp),
            fontWeight = FontWeight.ExtraBold
        )

        var message by rememberSaveable { mutableStateOf(uiModel.stageStepBarConfig.stageStepConfig.joinToString()) }
        var isError by rememberSaveable { mutableStateOf(false) }

        TextField(
            value = message,
            isError = isError,
            onValueChange = { value ->
                message = value
                try {
                    val stepsConfig = value.split(",").map {
                        it.trim().toInt()
                    }
                    if (stepsConfig.isEmpty()) {
                        isError = true
                    } else {
                        isError = false
                        onChanged(stepsConfig)
                    }
                } catch (e: NumberFormatException) {
                    isError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(top = 8.dp)
        )
        Text(
            modifier = Modifier.padding(top = 2.dp, start = 8.dp),
            text = "Comma separated list of integers",
            color = if (isError) {
                MaterialTheme.colors.error
            } else {
                MaterialTheme.colors.onSurface
            },
            fontSize = TextUnit(12f, type = TextUnitType.Sp)
        )
    }
}

@ExperimentalUnitApi
@Preview
@Composable
private fun Preview() {
    StepsInStagesListItem(uiModel = UiModel.default(), onChanged = {})
}