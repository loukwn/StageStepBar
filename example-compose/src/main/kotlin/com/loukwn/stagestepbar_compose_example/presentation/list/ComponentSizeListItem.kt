package com.loukwn.stagestepbar_compose_example.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
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
internal fun ComponentSizeListItem(
    uiModel: UiModel,
    modifier: Modifier,
    onThumbSizeChanged: (Float) -> Unit,
    onFilledTrackCrossAxisSizeChanged: (Float) -> Unit,
    onUnfilledTrackCrossAxisSizeChanged: (Float) -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                text = "Thumb Size",
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "${uiModel.stageStepBarConfig.thumbSize.value.toInt()}dp",
                fontSize = TextUnit(16f, TextUnitType.Sp)
            )
        }

        var thumbSize by rememberSaveable { mutableStateOf(uiModel.stageStepBarConfig.thumbSize.value) }

        Slider(
            value = thumbSize,
            onValueChange = {
                thumbSize = it
                onThumbSizeChanged(it)
            },
            valueRange = 0f..40.dp.value
        )

        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                text = "Filled Track Cross Axis Size",
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "${uiModel.stageStepBarConfig.crossAxisSizeFilledTrack.value.toInt()}dp",
                fontSize = TextUnit(16f, TextUnitType.Sp)
            )
        }

        var filledTrackSize by rememberSaveable { mutableStateOf(uiModel.stageStepBarConfig.crossAxisSizeFilledTrack.value) }

        Slider(
            value = filledTrackSize,
            onValueChange = {
                filledTrackSize = it
                onFilledTrackCrossAxisSizeChanged(it)
            },
            valueRange = 0f..12.dp.value
        )

        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                text = "Unfilled Track Cross Axis Size",
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "${uiModel.stageStepBarConfig.crossAxisSizeUnfilledTrack.value.toInt()}dp",
                fontSize = TextUnit(16f, TextUnitType.Sp)
            )
        }

        var unfilledTrackSize by rememberSaveable { mutableStateOf(uiModel.stageStepBarConfig.crossAxisSizeUnfilledTrack.value) }

        Slider(
            value = unfilledTrackSize,
            onValueChange = {
                unfilledTrackSize = it
                onUnfilledTrackCrossAxisSizeChanged(it)
            },
            valueRange = 0f..12.dp.value
        )
    }

}


@ExperimentalUnitApi
@Preview
@Composable
private fun Preview() {
    ComponentSizeListItem(
        uiModel = UiModel.default(),
        modifier = Modifier.width(300.dp),
        onThumbSizeChanged = {},
        onFilledTrackCrossAxisSizeChanged = {},
        onUnfilledTrackCrossAxisSizeChanged = {}
    )
}