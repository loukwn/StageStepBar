package com.loukwn.example_compose.presentation

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.loukwn.example_compose.ComponentType
import com.loukwn.example_compose.Interactions
import com.loukwn.example_compose.R
import com.loukwn.example_compose.UiModel
import com.loukwn.example_compose.presentation.list.*
import com.loukwn.example_compose.ui.theme.StageStepBarTheme
import com.loukwn.stagestepbar_compose.StageStepBar
import com.loukwn.stagestepbar_compose.data.Orientation
import com.loukwn.stagestepbar_compose.data.StageStepBarConfig

@ExperimentalUnitApi
@Composable
internal fun ExampleComposeUi(uiModel: UiModel, interactions: Interactions) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppToolbar(interactions)

        if (uiModel.stageStepBarConfig.orientation == Orientation.Horizontal) {
            StageStepBarArea(stageStepBarConfig = uiModel.stageStepBarConfig)
            Divider(color = MaterialTheme.colors.onSurface, thickness = 1.dp)
            ConfigListArea(uiModel, interactions)
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight(1f)
            ) {
                StageStepBarArea(stageStepBarConfig = uiModel.stageStepBarConfig)
                Divider(
                    color = MaterialTheme.colors.onSurface, modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                ConfigListArea(uiModel, interactions)
            }
        }
    }
}

@Composable
private fun AppToolbar(interactions: Interactions) {
    Row(
        modifier = Modifier.height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { interactions.onClosePressed() },
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close_24),
                contentDescription = "Button to go back",
            )
        }
        Text(
            text = "StageStepBar Compose Example",
            modifier = Modifier
                .weight(weight = 1f)
                .padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
private fun StageStepBarArea(stageStepBarConfig: StageStepBarConfig) {
    val modifier = when (stageStepBarConfig.orientation) {
        Orientation.Horizontal -> {
            Modifier
                .height(100.dp)
                .width(300.dp)
        }
        Orientation.Vertical -> {
            Modifier
                .height(300.dp)
                .width(100.dp)
        }
    }
    StageStepBar(
        modifier = modifier,
        config = stageStepBarConfig,
    )
}

@ExperimentalUnitApi
@Composable
private fun ConfigListArea(uiModel: UiModel, interactions: Interactions) {
    val listOfColors = listOf(
        Color.Magenta,
        Color.Green,
        Color.Red,
        Color.Yellow,
        Color(0xFF000000),
        Color(0xFFA9A9A9),
        Color(0xFF6F6F6F),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f),
        contentPadding = PaddingValues(32.dp)
    ) {

        // Steps in Stages Config
        item {
            StepsInStagesListItem(uiModel = uiModel) {
                interactions.onStepsInStagesConfigChanged(it)
            }
        }

        // State
        item {
            StateListItem(
                uiModel = uiModel,
                onNullToggleChanged = { interactions.onCurrentStateMadeNull(it) },
                onStageChanged = { interactions.onCurrentStateStageChanged(it) },
                onStepChanged = { interactions.onCurrentStateStepsChanged(it) }
            )
        }

        // Animate
        item {
            AnimateListItem(
                shouldAnimate = uiModel.stageStepBarConfig.shouldAnimate,
                animationDuration = (uiModel.stageStepBarConfig.animationSpec as TweenSpec).durationMillis,
                onAnimationStateChanged = { interactions.onAnimationStateChanged(it) },
                onAnimationDurationChanged = { interactions.onAnimationDurationChanged(it) }
            )
        }

        // Orientation / Direction
        item {
            OrientationDirectionListItem(
                uiModel = uiModel,
                modifier = Modifier.fillParentMaxWidth(1f),
                onOrientationSelected = { interactions.onOrientationSelected(it) },
                onHorizontalDirectionSelected = { interactions.onHorizontalDirectionSelected(it) },
                onVerticalDirectionSelected = { interactions.onVerticalDirectionSelected(it) }
            )
        }

        // Tracks / Thumbs drawables
        listOf(
            ComponentType.FilledTrack to uiModel.stageStepBarConfig.filledTrack,
            ComponentType.UnfilledTrack to uiModel.stageStepBarConfig.unfilledTrack,
            ComponentType.FilledThumb to uiModel.stageStepBarConfig.filledThumb,
            ComponentType.UnfilledThumb to uiModel.stageStepBarConfig.unfilledThumb,
        ).map { (componentType, drawnComponent) ->
            item {
                ComponentDrawableSelectionListItem(
                    listOfColors = listOfColors,
                    componentType = componentType,
                    drawnComponent = drawnComponent,
                    modifier = Modifier.fillParentMaxWidth(1f),
                    onComponentDrawableChanged = { position, color ->
                        interactions.onComponentDropdownSelectionChanged(
                            componentType,
                            position,
                            color
                        )
                    }
                )
            }
        }

        // Thumb / Track Sizes
        item {
            ComponentSizeListItem(
                uiModel = uiModel,
                modifier = Modifier.fillParentMaxWidth(1f),
                onThumbSizeChanged = {
                    interactions.onThumbSizeChanged(it)
                },
                onFilledTrackCrossAxisSizeChanged = {
                    interactions.onFilledTrackCrossAxisSizeChanged(it)
                },
                onUnfilledTrackCrossAxisSizeChanged = {
                    interactions.onUnfilledTrackCrossAxisSizeChanged(it)
                },
            )
        }

        // Toggle Thumbs
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    text = "Show Thumbs?",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontWeight = FontWeight.ExtraBold
                )
                Switch(checked = uiModel.stageStepBarConfig.showThumbs, onCheckedChange = {
                    interactions.onShowThumbsChanged(it)
                })
            }
        }

        // Toggle Thumbs
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    text = "Draw Tracks Behind Thumbs?",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontWeight = FontWeight.ExtraBold
                )
                Switch(checked = uiModel.stageStepBarConfig.drawTracksBehindThumbs, onCheckedChange = {
                    interactions.onDrawTracksBehindThumbsChanged(it)
                })
            }
        }
    }
}

@ExperimentalUnitApi
@Preview(device = Devices.PIXEL_3A)
@Composable
private fun PreviewUi() {
    StageStepBarTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colors.primary) {
            Box(modifier = Modifier.size(200.dp)) {
                StageStepBar(
                    config = StageStepBarConfig.default(),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(100.dp)
                )
            }
        }
    }
}