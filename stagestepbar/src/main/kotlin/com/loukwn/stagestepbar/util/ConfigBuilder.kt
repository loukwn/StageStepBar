package com.loukwn.stagestepbar.util

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import com.loukwn.stagestepbar.R
import com.loukwn.stagestepbar.StageStepBar
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

internal class ConfigBuilder {

    fun getFromAttributes(
        context: Context,
        attrArray: TypedArray,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig {
        var newConfig = oldConfig

        // Set StageStepConfig
        val attrStageStepConfig = attrArray.getString(R.styleable.StageStepBar_ssb_stageStepConfig)
        if (attrStageStepConfig != null) {
            try {
                val stageStepConfig = attrStageStepConfig.split(",").map { it.toInt() }
                newConfig = newConfig.copy(stageStepConfig = stageStepConfig)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Please make sure that your stageStepConfig is in the form of comma separated integers (e.g: '5,5,6')")
            }
        }

        // Set CurrentState
        val attrCurrentState = attrArray.getString(R.styleable.StageStepBar_ssb_currentState)
        if (attrCurrentState != null) {
            try {
                val currentState = attrCurrentState.split(",").map { it.toInt() }
                if (currentState.size != 2) {
                    throw IllegalArgumentException("The current state argument should be 2 comma separated integers")
                }
                newConfig = newConfig.copy(
                    currentState = StageStepBar.State(
                        stage = currentState[0],
                        currentState[1]
                    )
                )
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("The current state argument should be 2 comma separated integers")
            }
        }

        // Set Animate
        val animate =
            attrArray.getBoolean(R.styleable.StageStepBar_ssb_animate, oldConfig.shouldAnimate)
        newConfig = newConfig.copy(shouldAnimate = animate)

        // Set AnimationDuration
        val animationDuration =
            attrArray.getInteger(
                R.styleable.StageStepBar_ssb_animationDuration,
                oldConfig.animationDuration.toInt()
            )
        newConfig = newConfig.copy(animationDuration = animationDuration.toLong())

        // Set Orientation
        val orientation = attrArray.getEnum(
            R.styleable.StageStepBar_ssb_orientation,
            oldConfig.orientation
        )
        newConfig = newConfig.copy(orientation = orientation)

        // Set HorizontalDirection
        val horizontalDirection = attrArray.getEnum(
            R.styleable.StageStepBar_ssb_horizontalDirection,
            oldConfig.horizontalDirection
        )
        newConfig = newConfig.copy(horizontalDirection = horizontalDirection)

        // Set VerticalDirection
        val verticalDirection = attrArray.getEnum(
            R.styleable.StageStepBar_ssb_verticalDirection,
            oldConfig.verticalDirection
        )
        newConfig = newConfig.copy(verticalDirection = verticalDirection)

        // Set ThumbSize
        val thumbSize = attrArray.getDimensionPixelSize(
            R.styleable.StageStepBar_ssb_thumbSize,
            oldConfig.thumbSize
        )
        newConfig = newConfig.copy(thumbSize = thumbSize)

        // Set CrossAxisFilledTrackSize
        val crossAxisFilledTrackSize = attrArray.getDimensionPixelSize(
            R.styleable.StageStepBar_ssb_crossAxisFilledTrackSize,
            oldConfig.crossAxisSizeFilledTrack
        )
        newConfig = newConfig.copy(crossAxisSizeFilledTrack = crossAxisFilledTrackSize)

        // Set CrossAxisUnfilledTrackSize
        val crossAxisUnfilledTrackSize = attrArray.getDimensionPixelSize(
            R.styleable.StageStepBar_ssb_crossAxisUnfilledTrackSize,
            oldConfig.crossAxisSizeUnfilledTrack
        )
        newConfig = newConfig.copy(crossAxisSizeUnfilledTrack = crossAxisUnfilledTrackSize)

        // Set tracks and thumbs
        newConfig = newConfig.copy(
            filledTrack = getFilledTrackDrawnComponent(context, attrArray),
            unfilledTrack = getUnfilledTrackDrawnComponent(context, attrArray),
            filledThumb = getFilledThumbDrawnComponent(context, attrArray),
            unfilledThumb = getUnfilledThumbDrawnComponent(context, attrArray),
        )

        // Set ShowThumbs
        val showThumbs =
            attrArray.getBoolean(R.styleable.StageStepBar_ssb_showThumbs, oldConfig.showThumbs)
        newConfig = newConfig.copy(showThumbs = showThumbs)

        return newConfig
    }

    fun getWithNewStageStepConfig(
        stageStepConfig: List<Int>,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig {
        return when {
            stageStepConfig.isEmpty() -> throw IllegalArgumentException(
                "The stageStepConfig should have at least one element."
            )
            stageStepConfig.any { it <= 0 } -> throw IllegalArgumentException(
                "The stageStepConfig should only include positive integers."
            )
            else -> oldConfig.copy(stageStepConfig = stageStepConfig)
        }
    }

    fun getWithNewCurrentState(
        newState: StageStepBar.State?,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig {
        if (newState != null && (newState.stage < 0 || newState.step < 0)) {
            throw IllegalArgumentException("The state should only contain positive integers.")
        }

        return oldConfig.copy(currentState = newState)
    }

    fun getWithAnimate(
        animate: Boolean,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(shouldAnimate = animate)

    fun getWithAnimationDuration(
        animationDuration: Long,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(animationDuration = animationDuration)

    fun getWithOrientation(
        orientation: StageStepBar.Orientation,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(orientation = orientation)

    fun getWithHorizontalDirection(
        horizontalDirection: StageStepBar.HorizontalDirection,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(horizontalDirection = horizontalDirection)

    fun getWithVerticalDirection(
        verticalDirection: StageStepBar.VerticalDirection,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(verticalDirection = verticalDirection)

    fun getWithFilledThumbAsDrawable(
        drawable: Drawable,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(filledThumb = StageStepBar.DrawnComponent.UserProvided(drawable))

    fun getWithFilledThumbAsDefaultShape(
        color: Int,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(filledThumb = StageStepBar.DrawnComponent.Default(color))

    fun getWithUnfilledThumbAsDrawable(
        drawable: Drawable,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(unfilledThumb = StageStepBar.DrawnComponent.UserProvided(drawable))

    fun getWithUnfilledThumbAsDefaultShape(
        color: Int,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(unfilledThumb = StageStepBar.DrawnComponent.Default(color))

    fun getWithFilledTrackAsDrawable(
        drawable: Drawable,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(filledTrack = StageStepBar.DrawnComponent.UserProvided(drawable))

    fun getWithFilledTrackAsDefaultShape(
        color: Int,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(filledTrack = StageStepBar.DrawnComponent.Default(color))

    fun getWithUnfilledTrackAsDrawable(
        drawable: Drawable,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(unfilledTrack = StageStepBar.DrawnComponent.UserProvided(drawable))

    fun getWithUnfilledTrackAsDefaultShape(
        color: Int,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(unfilledTrack = StageStepBar.DrawnComponent.Default(color))

    fun getWithThumbSize(
        size: Int,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(thumbSize = size)

    fun getWithCrossAxisUnfilledTrackSize(
        size: Int,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(crossAxisSizeUnfilledTrack = size)

    fun getWithCrossAxisFilledTrackSize(
        size: Int,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(crossAxisSizeFilledTrack = size)

    fun getWithShowThumbs(
        visible: Boolean,
        oldConfig: StageStepBar.StageStepBarConfig
    ): StageStepBar.StageStepBarConfig =
        oldConfig.copy(showThumbs = visible)

    private fun getFilledTrackDrawnComponent(
        context: Context,
        attrArray: TypedArray
    ): StageStepBar.DrawnComponent {
        val drawable = attrArray.getDrawable(R.styleable.StageStepBar_ssb_filledTrackDrawable)
        val color = attrArray.getColor(
            R.styleable.StageStepBar_ssb_filledTrackColor,
            ResourceProvider.provideColor(context, R.color.default_track_filled_color)
        )

        return if (drawable == null) {
            StageStepBar.DrawnComponent.Default(color)
        } else {
            StageStepBar.DrawnComponent.UserProvided(drawable)
        }
    }

    private fun getUnfilledTrackDrawnComponent(
        context: Context,
        attrArray: TypedArray,
    ): StageStepBar.DrawnComponent {
        val drawable = attrArray.getDrawable(R.styleable.StageStepBar_ssb_unfilledTrackDrawable)
        val color = attrArray.getColor(
            R.styleable.StageStepBar_ssb_unfilledTrackColor,
            ResourceProvider.provideColor(context, R.color.default_track_unfilled_color)
        )

        return if (drawable == null) {
            StageStepBar.DrawnComponent.Default(color)
        } else {
            StageStepBar.DrawnComponent.UserProvided(drawable)
        }
    }

    private fun getFilledThumbDrawnComponent(
        context: Context,
        attrArray: TypedArray
    ): StageStepBar.DrawnComponent {
        val drawable = attrArray.getDrawable(R.styleable.StageStepBar_ssb_filledThumbDrawable)
        val color = attrArray.getColor(
            R.styleable.StageStepBar_ssb_filledThumbColor,
            ResourceProvider.provideColor(context, R.color.default_thumb_filled_color)
        )

        return if (drawable == null) {
            StageStepBar.DrawnComponent.Default(color)
        } else {
            StageStepBar.DrawnComponent.UserProvided(drawable)
        }
    }

    private fun getUnfilledThumbDrawnComponent(
        context: Context,
        attrArray: TypedArray
    ): StageStepBar.DrawnComponent {
        val drawable = attrArray.getDrawable(R.styleable.StageStepBar_ssb_unfilledThumbDrawable)
        val color = attrArray.getColor(
            R.styleable.StageStepBar_ssb_unfilledThumbColor,
            ResourceProvider.provideColor(context, R.color.default_thumb_unfilled_color)
        )

        return if (drawable == null) {
            StageStepBar.DrawnComponent.Default(color)
        } else {
            StageStepBar.DrawnComponent.UserProvided(drawable)
        }
    }
}

inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T) =
    getInt(index, -1).let { if (it >= 0) enumValues<T>()[it] else default }
