@file:Suppress("unused")

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
    ) : DrawnComponent()

    data class Bitmap(
        val imageBitmap: ImageBitmap,
        val colorFilter: ColorFilter? = null,
    ) : DrawnComponent()

    data class Default(
        val color: Color,
    ) : DrawnComponent()
}

@Keep
data class State(val stage: Int, val step: Int)

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
