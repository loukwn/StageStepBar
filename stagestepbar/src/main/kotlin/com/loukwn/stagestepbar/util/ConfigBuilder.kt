package com.loukwn.stagestepbar.util

import android.content.Context
import android.content.res.TypedArray
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
                throw IllegalArgumentException(
                    "Please make sure that your stageStepConfig is in the form of comma separated integers (e.g: '5,5,6')"
                )
            }
        }

        // Set CurrentState
        val attrCurrentState = attrArray.getString(R.styleable.StageStepBar_ssb_currentState)
        if (attrCurrentState != null) {
            if (attrCurrentState == "null") {
                newConfig = newConfig.copy(
                    currentState = null
                )
            } else {
                try {
                    val currentState = attrCurrentState.split(",").map { it.toInt() }
                    if (currentState.size != 2) {
                        throw IllegalArgumentException(
                            "The current state argument should be 2 comma separated integers"
                        )
                    }
                    newConfig = newConfig.copy(
                        currentState = StageStepBar.State(
                            stage = currentState[0],
                            currentState[1]
                        )
                    )
                } catch (e: NumberFormatException) {
                    throw IllegalArgumentException(
                        "The current state argument should be 2 comma separated integers or 'null'."
                    )
                }
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
            filledDoneThumb = getFilledDoneTrackDrawnComponent(context, attrArray),
            filledThumb = getFilledThumbDrawnComponent(context, attrArray),
            unfilledThumb = getUnfilledThumbDrawnComponent(context, attrArray),
        )

        // Set ShowThumbs
        val showThumbs =
            attrArray.getBoolean(R.styleable.StageStepBar_ssb_showThumbs, oldConfig.showThumbs)
        newConfig = newConfig.copy(showThumbs = showThumbs)

        // Set ShowThumbs
        val drawTracksBehindThumbs =
            attrArray.getBoolean(R.styleable.StageStepBar_ssb_drawTracksBehindThumbs, oldConfig.drawTracksBehindThumbs)
        newConfig = newConfig.copy(drawTracksBehindThumbs = drawTracksBehindThumbs)

        return newConfig
    }

    private fun getFilledTrackDrawnComponent(
        context: Context,
        attrArray: TypedArray
    ): StageStepBar.DrawnComponent {
        val drawable = attrArray.getDrawable(R.styleable.StageStepBar_ssb_filledTrackDrawable)
        val alpha = attrArray.getFloat(R.styleable.StageStepBar_ssb_filledTrackDrawableAlpha, 1f)
        val color = attrArray.getColor(
            R.styleable.StageStepBar_ssb_filledTrackColor,
            ResourceProvider.provideColor(context, R.color.default_track_filled_color)
        )

        return if (drawable == null) {
            StageStepBar.DrawnComponent.Default(color)
        } else {
            StageStepBar.DrawnComponent.UserProvided(drawable, alpha)
        }
    }

    private fun getFilledDoneTrackDrawnComponent(
        context: Context,
        attrArray: TypedArray
    ): StageStepBar.DrawnComponent {
        val drawable = attrArray.getDrawable(R.styleable.StageStepBar_ssb_filledDoneTrackDrawable)
        val alpha = attrArray.getFloat(R.styleable.StageStepBar_ssb_filledTrackDrawableAlpha, 1f)
        val color = attrArray.getColor(
            R.styleable.StageStepBar_ssb_filledTrackColor,
            ResourceProvider.provideColor(context, R.color.default_track_filled_color)
        )

        return if (drawable == null) {
            StageStepBar.DrawnComponent.Default(color)
        } else {
            StageStepBar.DrawnComponent.UserProvided(drawable, alpha)
        }
    }

    private fun getUnfilledTrackDrawnComponent(
        context: Context,
        attrArray: TypedArray,
    ): StageStepBar.DrawnComponent {
        val drawable = attrArray.getDrawable(R.styleable.StageStepBar_ssb_unfilledTrackDrawable)
        val alpha = attrArray.getFloat(R.styleable.StageStepBar_ssb_unfilledTrackDrawableAlpha, 1f)
        val color = attrArray.getColor(
            R.styleable.StageStepBar_ssb_unfilledTrackColor,
            ResourceProvider.provideColor(context, R.color.default_track_unfilled_color)
        )

        return if (drawable == null) {
            StageStepBar.DrawnComponent.Default(color)
        } else {
            StageStepBar.DrawnComponent.UserProvided(drawable, alpha)
        }
    }

    private fun getFilledThumbDrawnComponent(
        context: Context,
        attrArray: TypedArray
    ): StageStepBar.DrawnComponent {
        val drawable = attrArray.getDrawable(R.styleable.StageStepBar_ssb_filledThumbDrawable)
        val alpha = attrArray.getFloat(R.styleable.StageStepBar_ssb_filledThumbDrawableAlpha, 1f)
        val color = attrArray.getColor(
            R.styleable.StageStepBar_ssb_filledThumbColor,
            ResourceProvider.provideColor(context, R.color.default_thumb_filled_color)
        )

        return if (drawable == null) {
            StageStepBar.DrawnComponent.Default(color)
        } else {
            StageStepBar.DrawnComponent.UserProvided(drawable, alpha)
        }
    }

    private fun getUnfilledThumbDrawnComponent(
        context: Context,
        attrArray: TypedArray
    ): StageStepBar.DrawnComponent {
        val drawable = attrArray.getDrawable(R.styleable.StageStepBar_ssb_unfilledThumbDrawable)
        val alpha = attrArray.getFloat(R.styleable.StageStepBar_ssb_unfilledThumbDrawableAlpha, 1f)
        val color = attrArray.getColor(
            R.styleable.StageStepBar_ssb_unfilledThumbColor,
            ResourceProvider.provideColor(context, R.color.default_thumb_unfilled_color)
        )

        return if (drawable == null) {
            StageStepBar.DrawnComponent.Default(color)
        } else {
            StageStepBar.DrawnComponent.UserProvided(drawable, alpha)
        }
    }
}

private inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T) =
    getInt(index, -1).let { if (it >= 0) enumValues<T>()[it] else default }
