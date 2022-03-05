package com.loukwn.example_compose

import android.app.Application
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.loukwn.stagestepbar_compose.data.*
import kotlinx.coroutines.flow.MutableStateFlow

class ExampleComposeViewModel(application: Application): AndroidViewModel(application), ViewModelContract {
    override val uiModels = MutableStateFlow(UiModel.default())
    override val events: MutableStateFlow<Event?> = MutableStateFlow(null)

    private val uiModel: UiModel
        get() = uiModels.value

    private val resources: Resources
        get() = getApplication<Application>().resources

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

        uiModels.value = uiModel.copy(stageStepBarConfig = newStageStepBarConfig)
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

    override fun onDrawBehindThumbsChanged(value: Boolean) {

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


}