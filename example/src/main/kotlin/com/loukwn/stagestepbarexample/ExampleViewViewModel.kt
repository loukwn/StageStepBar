package com.loukwn.stagestepbarexample

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.loukwn.stagestepbar.StageStepBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ExampleViewViewModel(application: Application) : AndroidViewModel(application) {
    private val _events: MutableStateFlow<Event?> = MutableStateFlow(null)
    val events: StateFlow<Event?> = _events

    private var currentState: StageStepBar.State = StageStepBar.State(2, 3)

    private val availableColors by lazy {
        listOf(
            Color.parseColor("#FFBB86FC"),
            Color.parseColor("#FF03DAC5"),
            Color.GRAY,
            Color.MAGENTA,
            Color.GREEN,
            Color.RED,
            Color.YELLOW
        )
    }
    private var filledTrackDefaultColorSelectionIndex = 0
    private var unfilledTrackDefaultColorSelectionIndex = 2
    private var filledThumbDefaultColorSelectionIndex = 0
    private var unfilledThumbDefaultColorSelectionIndex = 2

    internal val firstFilledTrackColor = availableColors[filledTrackDefaultColorSelectionIndex]
    internal val firstUnfilledTrackColor = availableColors[unfilledTrackDefaultColorSelectionIndex]
    internal val firstFilledThumbColor = availableColors[filledThumbDefaultColorSelectionIndex]
    internal val firstUnfilledThumbColor = availableColors[unfilledThumbDefaultColorSelectionIndex]


    fun stepsInStagesChanged(text: String) {
        try {
            val config = text.split(",").map {
                it.trim().toInt()
            }
            _events.value = Event.StepsInStagesConfigChanged(StepsInStagesChange.Valid(config))
        } catch (e: NumberFormatException) {
            val error = "Comma separated list of integers"
            _events.value = Event.StepsInStagesConfigChanged(StepsInStagesChange.Invalid(error))
        }
    }

    fun currentStageChanged(stage: String) {
        try {
            currentState = currentState?.copy(
                stage = stage.toInt()
            )

            _events.value = Event.CurrentStateChanged(CurrentStateChange.Valid(currentState))
        } catch (e: Exception) {
            val error = "Positive integer required"
            _events.value = Event.CurrentStateChanged(CurrentStateChange.Invalid(error, true))
        }
    }

    fun currentStepChanged(step: String) {
        try {
            currentState = currentState?.copy(
                step = step.toInt()
            )

            _events.value = Event.CurrentStateChanged(CurrentStateChange.Valid(currentState))
        } catch (e: Exception) {
            val error = "Positive integer required"
            _events.value = Event.CurrentStateChanged(CurrentStateChange.Invalid(error, false))
        }
    }

    fun stateNullToggled(isNull: Boolean) {
        _events.value =
            Event.CurrentStateChanged(CurrentStateChange.Valid(if (isNull) null else currentState))
    }

    fun animateToggled(value: Boolean) {
        _events.value = Event.AnimateChanged(value)
    }

    fun animationDurationChanged(text: String) {
        val animationDuration = if (text.isEmpty()) 0 else text.toLong()
        _events.value = Event.AnimationDurationChanged(animationDuration)
    }

    fun orientationChanged(selectionPosition: Int) {
        when (selectionPosition) {
            0 -> _events.value = Event.OrientationChanged(StageStepBar.Orientation.Horizontal)
            1 -> _events.value = Event.OrientationChanged(StageStepBar.Orientation.Vertical)
        }
    }

    fun horizontalDirectionChanged(selectionPosition: Int) {
        when (selectionPosition) {
            0 -> _events.value =
                Event.HorizontalDirectionChanged(StageStepBar.HorizontalDirection.Auto)
            1 -> _events.value =
                Event.HorizontalDirectionChanged(StageStepBar.HorizontalDirection.Ltr)
            2 -> _events.value =
                Event.HorizontalDirectionChanged(StageStepBar.HorizontalDirection.Rtl)
        }
    }

    fun verticalDirectionChanged(selectionPosition: Int) {
        when (selectionPosition) {
            0 -> _events.value = Event.VerticalDirectionChanged(StageStepBar.VerticalDirection.Btt)
            1 -> _events.value = Event.VerticalDirectionChanged(StageStepBar.VerticalDirection.Ttb)

        }
    }

    fun filledTrackDropdownSelected(selectionPosition: Int) {
        when (selectionPosition) {
            0 -> _events.value =
                Event.FilledTrackSetToDefault(availableColors[filledTrackDefaultColorSelectionIndex])
            1 -> _events.value =
                Event.FilledTrackSetToCustom(getDrawable(R.drawable.gradient_drawable))
        }
    }

    fun unfilledTrackDropdownSelected(selectionPosition: Int) {
        when (selectionPosition) {
            0 -> _events.value =
                Event.UnfilledTrackSetToDefault(availableColors[unfilledTrackDefaultColorSelectionIndex])
            1 -> _events.value =
                Event.UnfilledTrackSetToCustom(getDrawable(R.drawable.gradient_drawable_2))
        }
    }

    fun filledThumbDropdownSelected(selectionPosition: Int) {
        when (selectionPosition) {
            0 -> _events.value =
                Event.FilledThumbSetToDefault(availableColors[filledThumbDefaultColorSelectionIndex])
            1 -> _events.value =
                Event.FilledThumbSetToCustom(getDrawable(R.drawable.custom_shape_drawable))
        }
    }

    fun unfilledThumbDropdownSelected(selectionPosition: Int) {
        when (selectionPosition) {
            0 -> _events.value =
                Event.UnfilledThumbSetToDefault(availableColors[unfilledThumbDefaultColorSelectionIndex])
            1 -> _events.value =
                Event.UnfilledThumbSetToCustom(getDrawable(R.drawable.custom_shape_drawable_2))
        }
    }

    fun filledTrackColorViewClicked() {
        filledTrackDefaultColorSelectionIndex =
            (filledTrackDefaultColorSelectionIndex + 1) % availableColors.size
        _events.value =
            Event.FilledTrackSetToDefault(availableColors[filledTrackDefaultColorSelectionIndex])
    }

    fun unfilledTrackColorViewClicked() {
        unfilledTrackDefaultColorSelectionIndex =
            (unfilledTrackDefaultColorSelectionIndex + 1) % availableColors.size
        _events.value =
            Event.UnfilledTrackSetToDefault(availableColors[unfilledTrackDefaultColorSelectionIndex])
    }

    fun filledThumbColorViewClicked() {
        filledThumbDefaultColorSelectionIndex =
            (filledThumbDefaultColorSelectionIndex + 1) % availableColors.size
        _events.value =
            Event.FilledThumbSetToDefault(availableColors[filledThumbDefaultColorSelectionIndex])
    }

    fun unfilledThumbColorViewClicked() {
        unfilledThumbDefaultColorSelectionIndex =
            (unfilledThumbDefaultColorSelectionIndex + 1) % availableColors.size
        _events.value =
            Event.UnfilledThumbSetToDefault(availableColors[unfilledThumbDefaultColorSelectionIndex])
    }

    fun thumbSizeChanged(seekBarProgress: Int) {
        val value = seekBarProgress * 2 * DEFAULT_THUMB_SIZE_DP / 100
        _events.value = Event.ThumbSizeChanged(value)
    }

    fun filledTrackSizeChanged(seekBarProgress: Int) {
        val value = seekBarProgress * 2 * DEFAULT_FILLED_TRACK_SIZE_DP / 100
        _events.value = Event.FilledTrackCrossAxisSizeChanged(value)
    }

    fun unfilledTrackSizeChanged(seekBarProgress: Int) {
        val value = seekBarProgress * 2 * DEFAULT_UNFILLED_TRACK_SIZE_DP / 100
        _events.value = Event.UnfilledTrackCrossAxisSizeChanged(value)
    }

    fun showThumbsToggled(value: Boolean) {
        _events.value = Event.ShowThumbsChanged(value)
    }

    fun drawTracksBehindThumbsChanged(value: Boolean) {
        _events.value = Event.DrawTracksBehindThumbsChanged(value)
    }

    private fun getDrawable(@DrawableRes drawableRes: Int): Drawable =
        ContextCompat.getDrawable(
            getApplication(),
            drawableRes
        )!!

    companion object {
        internal const val DEFAULT_THUMB_SIZE_DP = 20
        internal const val DEFAULT_FILLED_TRACK_SIZE_DP = 6
        internal const val DEFAULT_UNFILLED_TRACK_SIZE_DP = 6
    }

}

internal sealed class StepsInStagesChange {
    data class Valid(val config: List<Int>) : StepsInStagesChange()
    data class Invalid(val error: String) : StepsInStagesChange()
}

internal sealed class CurrentStateChange {
    data class Valid(val state: StageStepBar.State?) : CurrentStateChange()
    data class Invalid(val error: String, val isForStage: Boolean) : CurrentStateChange()
}

internal sealed class Event {
    data class StepsInStagesConfigChanged(val change: StepsInStagesChange) : Event()
    data class CurrentStateChanged(val change: CurrentStateChange) : Event()
    data class AnimateChanged(val animate: Boolean) : Event()
    data class AnimationDurationChanged(val animationDuration: Long) : Event()
    data class OrientationChanged(val orientation: StageStepBar.Orientation) : Event()
    data class HorizontalDirectionChanged(val hDirection: StageStepBar.HorizontalDirection) :
        Event()

    data class VerticalDirectionChanged(val vDirection: StageStepBar.VerticalDirection) : Event()
    data class FilledTrackSetToDefault(@ColorInt val color: Int) : Event()
    data class FilledTrackSetToCustom(val drawable: Drawable) : Event()
    data class UnfilledTrackSetToDefault(@ColorInt val color: Int) : Event()
    data class UnfilledTrackSetToCustom(val drawable: Drawable) : Event()
    data class FilledThumbSetToDefault(@ColorInt val color: Int) : Event()
    data class FilledThumbSetToCustom(val drawable: Drawable) : Event()
    data class UnfilledThumbSetToDefault(@ColorInt val color: Int) : Event()
    data class UnfilledThumbSetToCustom(val drawable: Drawable) : Event()
    data class ThumbSizeChanged(val sizeInPixel: Int) : Event()
    data class FilledTrackCrossAxisSizeChanged(val sizeInPixel: Int) : Event()
    data class UnfilledTrackCrossAxisSizeChanged(val sizeInPixel: Int) : Event()
    data class ShowThumbsChanged(val enabled: Boolean) : Event()
    data class DrawTracksBehindThumbsChanged(val enabled: Boolean) : Event()
}
