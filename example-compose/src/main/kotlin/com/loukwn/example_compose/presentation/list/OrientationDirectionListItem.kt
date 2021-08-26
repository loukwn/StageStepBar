package com.loukwn.example_compose.presentation.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.loukwn.example_compose.UiModel
import com.loukwn.example_compose.presentation.common.DropDown
import com.loukwn.stagestepbar_compose.data.HorizontalDirection
import com.loukwn.stagestepbar_compose.data.Orientation
import com.loukwn.stagestepbar_compose.data.VerticalDirection

@ExperimentalUnitApi
@Composable
internal fun OrientationDirectionListItem(
    uiModel: UiModel,
    modifier: Modifier,
    onOrientationSelected: (Int) -> Unit,
    onHorizontalDirectionSelected: (Int) -> Unit,
    onVerticalDirectionSelected: (Int) -> Unit,
) {
    Column {
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            text = "Orientation/Direction",
            fontSize = TextUnit(18f, TextUnitType.Sp),
            fontWeight = FontWeight.ExtraBold
        )

        DropDown(
            modifier = modifier,
            label = "Orientation",
            initialPosition = if (uiModel.stageStepBarConfig.orientation == Orientation.Horizontal) {
                0
            } else {
                1
            },
            options = listOf("Horizontal", "Vertical"),
            onSelectionMade = { position ->
                onOrientationSelected(position)
            }
        )

        DropDown(
            modifier = modifier,
            label = "Horizontal Direction",
            initialPosition = when (uiModel.stageStepBarConfig.horizontalDirection) {
                HorizontalDirection.Auto -> 0
                HorizontalDirection.Ltr -> 1
                HorizontalDirection.Rtl -> 2
            },
            options = listOf("Auto", "Left to Right", "Right to Left"),
            onSelectionMade = { position ->
                onHorizontalDirectionSelected(position)
            }
        )

        DropDown(
            modifier = modifier,
            label = "Vertical Direction",
            initialPosition = when (uiModel.stageStepBarConfig.verticalDirection) {
                VerticalDirection.Ttb -> 0
                VerticalDirection.Btt -> 1
            },
            options = listOf("Bottom to Top", "Top to Bottom"),
            onSelectionMade = { position ->
                onVerticalDirectionSelected(position)
            }
        )
    }
}

@ExperimentalUnitApi
@Preview
@Composable
private fun Preview() {
    OrientationDirectionListItem(
        uiModel = UiModel.default(),
        modifier = Modifier.width(200.dp),
        onOrientationSelected = {},
        onHorizontalDirectionSelected = {},
        onVerticalDirectionSelected = {},
    )
}