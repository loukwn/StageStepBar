package com.loukwn.stagestepbar_compose_example

import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.loukwn.stagestepbar_compose.data.DrawnComponent
import com.loukwn.stagestepbar_compose.data.HorizontalDirection
import com.loukwn.stagestepbar_compose.data.Orientation
import com.loukwn.stagestepbar_compose.data.State
import com.loukwn.stagestepbar_compose.data.VerticalDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class ExampleComposeViewModel: ViewModel(), ViewModelContract {
    override val uiModels = MutableStateFlow(UiModel.default())
    override val events: MutableStateFlow<Event?> = MutableStateFlow(null)

    private val uiModel: UiModel
        get() = uiModels.value

    override fun onClosePressed() {
        events.value = Event.Close
    }

    override fun onStepsInStagesConfigChanged(value: List<Int>) {
        uiModels.value = uiModel.copy(
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
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(currentState = state)
        )
    }

    override fun onCurrentStateStageChanged(value: Int) {
        uiModels.value = uiModel.copy(
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
        uiModels.value = uiModel.copy(
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
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(shouldAnimate = value),
        )
    }

    override fun onAnimationDurationChanged(value: Int) {
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(animationSpec = tween(value)),
        )
    }

    override fun onOrientationSelected(position: Int) {
        val orientation = if (position == 0) Orientation.Horizontal else Orientation.Vertical
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(orientation = orientation),
        )
    }

    override fun onHorizontalDirectionSelected(position: Int) {
        val horizontalDirection = when (position) {
            0 -> HorizontalDirection.Auto
            1 -> HorizontalDirection.Ltr
            else -> HorizontalDirection.Rtl
        }
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(horizontalDirection = horizontalDirection),
        )
    }

    override fun onVerticalDirectionSelected(position: Int) {
        val verticalDirection = when (position) {
            0 -> VerticalDirection.Btt
            else -> VerticalDirection.Ttb
        }
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(verticalDirection = verticalDirection),
        )
    }

    override fun onComponentDropdownSelectionChanged(
        component: ComponentType,
        position: Int,
        color: Color
    ) {
        val oldStepBarModel = uiModel.stageStepBarConfig

        when (component) {
            ComponentType.FilledTrack,
            ComponentType.UnfilledTrack -> {
                val drawnComponent = when (position) {
                    0 -> DrawnComponent.Default(color = color)
                    1 -> DrawnComponent.Drawable(drawableRes = R.drawable.gradient_drawable)
                    else -> DrawnComponent.Drawable(drawableRes = R.drawable.gradient_drawable_2)
                }
                uiModels.update {
                    if (component == ComponentType.FilledTrack) {
                        it.copy(stageStepBarConfig = oldStepBarModel.copy(filledTrack = drawnComponent))
                    } else {
                        it.copy(stageStepBarConfig = oldStepBarModel.copy(unfilledTrack = drawnComponent))
                    }
                }
            }
            ComponentType.FilledThumb,
            ComponentType.UnfilledThumb -> {
                val drawnComponent = when (position) {
                    0 -> DrawnComponent.Default(color = color)
                    1 -> DrawnComponent.Drawable(drawableRes = R.drawable.custom_shape_drawable)
                    else -> DrawnComponent.Drawable(drawableRes = R.drawable.custom_shape_drawable_2)
                }

                uiModels.update {
                    if (component == ComponentType.FilledThumb) {
                        it.copy(stageStepBarConfig = oldStepBarModel.copy(filledThumb = drawnComponent))
                    } else {
                        it.copy(stageStepBarConfig = oldStepBarModel.copy(unfilledThumb = drawnComponent))
                    }
                }
            }
            ComponentType.ActiveThumb -> {
                val drawnComponent = when (position) {
                    0 -> null
                    1 -> DrawnComponent.Default(color = color)
                    2 -> DrawnComponent.Drawable(drawableRes = R.drawable.custom_shape_drawable)
                    else -> DrawnComponent.Drawable(drawableRes = R.drawable.custom_shape_drawable_2)
                }

                uiModels.update { it.copy(stageStepBarConfig = oldStepBarModel.copy(activeThumb = drawnComponent)) }
            }
        }
    }

    override fun onThumbSizeChanged(value: Float) {
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                thumbSize = value.toInt().dp
            )
        )
    }

    override fun onFilledTrackCrossAxisSizeChanged(value: Float) {
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                crossAxisSizeFilledTrack = value.toInt().dp
            )
        )
    }

    override fun onUnfilledTrackCrossAxisSizeChanged(value: Float) {
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                crossAxisSizeUnfilledTrack = value.toInt().dp
            )
        )
    }

    override fun onShowThumbsChanged(enabled: Boolean) {
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                showThumbs = enabled
            )
        )
    }

    override fun onDrawTracksBehindThumbsChanged(value: Boolean) {
        uiModels.value = uiModel.copy(
            stageStepBarConfig = uiModel.stageStepBarConfig.copy(
                drawTracksBehindThumbs = value
            )
        )
    }
}