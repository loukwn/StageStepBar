package com.loukwn.stagestepbar_compose.data

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Keep
enum class Orientation {
    Horizontal, Vertical
}

@Keep
enum class HorizontalDirection {
    Auto, Ltr, Rtl
}

@Keep
enum class VerticalDirection {
    Ttb, Btt
}

@Keep
sealed class DrawnComponent {
    data class Drawable(
        @DrawableRes val drawableRes: Int,
        val colorFilter: ColorFilter? = null,
        val size: IntSize? = null,
        val alpha: Float = 1f,
    ) : DrawnComponent()

    data class Bitmap(
        val imageBitmap: ImageBitmap,
        val colorFilter: ColorFilter? = null,
        val alpha: Float = 1f,
    ) : DrawnComponent()

    data class Default(
        val color: Color,
    ) : DrawnComponent()
}

@Keep
data class State(val stage: Int, val step: Int)

/**
 * @param stageStepConfig This is a list that describes the steps per stage.
 * For example a value of 6,4,3 means 6 steps from stage 0 to 1, 4 from stage 1 to 2
 * and 3 steps from stage 2 to 3.
 *
 * @param currentState This is a list that describes the current state. For example a value of 2,4
 * means go to stage 2, step 4. Both the stage and the step provided here are coerced to the
 * largest possible value (according to stageStepConfig). A null value here means that nothing is
 * filled.
 *
 * @param shouldAnimate Whether or not the progress change will animate.
 *
 * @param animationSpec The configuration of the animation.
 *
 * @param orientation Whether the bar is horizontal or vertical.
 *
 * @param horizontalDirection The direction of the bar if it is horizontal.
 * For possible values see [HorizontalDirection].
 *
 * @param verticalDirection The direction of the bar if it is vertical.
 * For possilble values see [VerticalDirection].
 *
 * @param filledTrack Filled track is the bar that goes from the start all the way to the [currentState].
 * Essentially represents the completed part of the bar. This field describes how it should look
 * like (See [DrawnComponent] for more).
 *
 * @param unfilledTrack Unfilled track is the bar that goes from the [currentState] to the end.
 * Essentially represents the NON completed part of the bar. This field describes how it should look
 * like (See [DrawnComponent] for more).
 *
 * @param activeThumb Represents how the latest filled (completed) stage should look like.
 * If it is null then that stage's look will be described by [filledThumb].
 *
 * @param filledThumb Represents how the filled (completed) stages should look like.
 * (See [DrawnComponent] for more).
 *
 * @param unfilledThumb Represents how the unfilled (completed) stages should look like.
 * (See [DrawnComponent] for more).
 *
 * @param thumbSize The size of the thumbs (both filled and unfilled).
 *
 * @param crossAxisSizeFilledTrack The height of the filled track if the bar is horizontal or its
 * width if it is vertical.
 *
 * @param crossAxisSizeUnfilledTrack The height of the unfilled track if the bar is horizontal or
 * its width if it is vertical.
 *
 * @param showThumbs Whether or not to show the thumbs.
 *
 * @param drawTracksBehindThumbs This option addresses the cases where we might not want to see the
 * track behind the thumb (e.g semi transparent thumb).
 */
@Keep
@Immutable
data class StageStepBarConfig(
    val stageStepConfig: List<Int>,
    val currentState: State?,
    val shouldAnimate: Boolean,
    val animationSpec: AnimationSpec<Float>,
    val orientation: Orientation,
    val horizontalDirection: HorizontalDirection,
    val verticalDirection: VerticalDirection,
    val filledTrack: DrawnComponent,
    val unfilledTrack: DrawnComponent,
    val activeThumb: DrawnComponent?,
    val filledThumb: DrawnComponent,
    val unfilledThumb: DrawnComponent,
    val thumbSize: Dp,
    val crossAxisSizeFilledTrack: Dp,
    val crossAxisSizeUnfilledTrack: Dp,
    val showThumbs: Boolean,
    val drawTracksBehindThumbs: Boolean,
) {
    internal val numOfStages: Int
        get() = stageStepConfig.size + 1

    companion object {
        fun default(): StageStepBarConfig =
            StageStepBarConfig(
                stageStepConfig = listOf(1),
                currentState = null,
                shouldAnimate = true,
                animationSpec = tween(500),
                orientation = Orientation.Horizontal,
                horizontalDirection = HorizontalDirection.Auto,
                verticalDirection = VerticalDirection.Btt,
                filledTrack = DrawnComponent.Default(
                    color = Color(0xFF000000)
                ),
                unfilledTrack = DrawnComponent.Default(
                    color = Color(0xFFA9A9A9)
                ),
                activeThumb = null,
                filledThumb = DrawnComponent.Default(
                    color = Color(0xFF000000)
                ),
                unfilledThumb = DrawnComponent.Default(
                    color = Color(0xFF6F6F6F)
                ),
                thumbSize = 20.dp,
                crossAxisSizeFilledTrack = 6.dp,
                crossAxisSizeUnfilledTrack = 6.dp,
                showThumbs = true,
                drawTracksBehindThumbs = true,
            )
    }
}
