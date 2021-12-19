package com.loukwn.example_compose.presentation

import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.loukwn.example_compose.ComponentType
import com.loukwn.example_compose.Interactions
import com.loukwn.example_compose.R
import com.loukwn.example_compose.UiModel
import com.loukwn.stagestepbar_compose.data.*
import com.loukwn.stagestepbar_compose.data.State

@ExperimentalUnitApi
@Composable
internal fun MainUi(onClose: () -> Unit) {
    val resources = LocalContext.current.resources

    var uiModel by remember {
        mutableStateOf(
            UiModel.default().copy(
                stageStepBarConfig = StageStepBarConfig.default()
                    .copy(
                        stageStepConfig = listOf(5, 5, 5),
                        filledTrack = DrawnComponent.Default(
                            color = Color.Magenta
                        ),
                        unfilledTrack = DrawnComponent.Default(
                            color = Color.Gray
                        ),
                        filledThumb = DrawnComponent.Default(
                            color = Color.Magenta
                        ),
                        unfilledThumb = DrawnComponent.Default(
                            color = Color.Gray
                        ),
                    ),
                currentStage = 2,
                currentStep = 3,
            )
        )
    }

    val interactions = object : Interactions {
        override fun onClosePressed() {
            onClose()
        }

        override fun onStepsInStagesConfigChanged(value: List<Int>) {
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                    stageStepConfig = value
                )
            )
        }

        override fun onCurrentStateMadeNull(isNull: Boolean) {
            val state = if (isNull) {
                null
            } else {
                State(uiModel.currentStage, uiModel.currentStep)
            }
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(currentState = state)
            )
        }

        override fun onCurrentStateStageChanged(value: Int) {
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                    currentState = State(
                        value,
                        uiModel.currentStep
                    )
                ),
                currentStage = value
            )
        }

        override fun onCurrentStateStepsChanged(value: Int) {
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                    currentState = State(
                        uiModel.currentStage,
                        value
                    )
                ),
                currentStep = value
            )
        }

        override fun onAnimationStateChanged(value: Boolean) {
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(shouldAnimate = value),
            )
        }

        override fun onAnimationDurationChanged(value: Int) {
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(animationSpec = tween(value)),
            )
        }

        override fun onOrientationSelected(position: Int) {
            val orientation = if (position == 0) Orientation.Horizontal else Orientation.Vertical
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(orientation = orientation),
            )
        }

        override fun onHorizontalDirectionSelected(position: Int) {
            val horizontalDirection = when (position) {
                0 -> HorizontalDirection.Auto
                1 -> HorizontalDirection.Ltr
                else -> HorizontalDirection.Rtl
            }
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(horizontalDirection = horizontalDirection),
            )
        }

        override fun onVerticalDirectionSelected(position: Int) {
            val verticalDirection = when (position) {
                0 -> VerticalDirection.Btt
                else -> VerticalDirection.Ttb
            }
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(verticalDirection = verticalDirection),
            )
        }

        override fun onComponentDropdownSelectionChanged(
            component: ComponentType,
            position: Int,
            color: Color
        ) {
            val drawnComponent = if (position == 0) {
                DrawnComponent.Default(
                    color = color
                )
            } else {
                val imageBitmap = getImageBitmapFromResources(resources, component, position)

                DrawnComponent.UserProvided(
                    imageBitmap = imageBitmap,
                )
            }
            val oldStepBarModel = uiModel.stageStepBarConfig

            val newStageStepBarConfig = when (component) {
                ComponentType.FilledTrack -> oldStepBarModel.copy(filledTrack = drawnComponent)
                ComponentType.UnfilledTrack -> oldStepBarModel.copy(unfilledTrack = drawnComponent)
                ComponentType.FilledThumb -> oldStepBarModel.copy(filledThumb = drawnComponent)
                ComponentType.UnfilledThumb -> oldStepBarModel.copy(unfilledThumb = drawnComponent)
            }

            uiModel = uiModel.copy(stageStepBarConfig = newStageStepBarConfig)
        }

        override fun onThumbSizeChanged(value: Float) {
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                    thumbSize = value.toInt().dp
                )
            )
        }

        override fun onFilledTrackCrossAxisSizeChanged(value: Float) {
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                    crossAxisSizeFilledTrack = value.toInt().dp
                )
            )
        }

        override fun onUnfilledTrackCrossAxisSizeChanged(value: Float) {
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                    crossAxisSizeUnfilledTrack = value.toInt().dp
                )
            )
        }

        override fun onShowThumbsChanged(enabled: Boolean) {
            uiModel = uiModel.copy(
                stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                    showThumbs = enabled
                )
            )
        }
    }

    InternalMainUi(
        uiModel,
        interactions,
    )
}


private fun getImageBitmapFromResources(
    resources: Resources,
    component: ComponentType,
    positionOnDropdown: Int
): ImageBitmap {
    return when (component) {
        ComponentType.FilledTrack,
        ComponentType.UnfilledTrack -> {
            val drawableRes = if (positionOnDropdown == 1) {
                R.drawable.gradient_drawable
            } else {
                R.drawable.gradient_drawable_2
            }

            val drawable = requireNotNull(
                ResourcesCompat.getDrawable(
                    resources,
                    drawableRes,
                    null
                )
            )

            (drawable as GradientDrawable).toBitmap(40, 40).asImageBitmap()
        }
        ComponentType.FilledThumb,
        ComponentType.UnfilledThumb -> {
            val drawableRes = if (positionOnDropdown == 1) {
                R.drawable.custom_shape_drawable
            } else {
                R.drawable.custom_shape_drawable_2
            }

            val drawable = requireNotNull(
                ResourcesCompat.getDrawable(
                    resources,
                    drawableRes,
                    null
                )
            )

            drawable.toBitmap().asImageBitmap()
        }
    }
}
