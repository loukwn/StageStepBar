package com.loukwn.stagestepbar_compose

import androidx.annotation.Keep
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.loukwn.stagestepbar_compose.data.DrawnComponent
import com.loukwn.stagestepbar_compose.data.HorizontalDirection
import com.loukwn.stagestepbar_compose.data.Orientation
import com.loukwn.stagestepbar_compose.data.StageStepBarConfig
import com.loukwn.stagestepbar_compose.data.State
import com.loukwn.stagestepbar_compose.data.ThumbConfig
import com.loukwn.stagestepbar_compose.data.VerticalDirection
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
@Keep
fun StageStepBar(
    modifier: Modifier = Modifier,
    stageStepConfig: List<Int> = listOf(1),
    currentState: State? = null,
    shouldAnimate: Boolean = true,
    animationSpec: AnimationSpec<Float> = tween(500),
    orientation: Orientation = Orientation.Horizontal,
    horizontalDirection: HorizontalDirection = HorizontalDirection.Auto,
    verticalDirection: VerticalDirection = VerticalDirection.Btt,
    filledTrack: DrawnComponent = DrawnComponent.Default(color = Color(0xFF000000)),
    unfilledTrack: DrawnComponent = DrawnComponent.Default(color = Color(0xFFA9A9A9)),
    filledThumb: ThumbConfig = ThumbConfig(listOf(DrawnComponent.Default(Color(0xFF000000)))),
    unfilledThumb: ThumbConfig = ThumbConfig(listOf(DrawnComponent.Default(color = Color(0xFF6F6F6F)))),
    thumbSize: Dp = 20.dp,
    crossAxisSizeFilledTrack: Dp = 6.dp,
    crossAxisSizeUnfilledTrack: Dp = 6.dp,
    showThumbs: Boolean = true,
    drawTracksBehindThumbs: Boolean = true,
) {
    LaunchedEffect(stageStepConfig, currentState, filledThumb, unfilledThumb) {
        validateInput(stageStepConfig, currentState, filledThumb, unfilledThumb)
    }

    val finalProgress = calculateProgress(stageStepConfig, currentState)
    val currentProgress = if (!shouldAnimate) {
        rememberSaveable(currentState, stageStepConfig) { mutableFloatStateOf(finalProgress) }
    } else {
        animateFloatAsState(
            targetValue = finalProgress,
            animationSpec = animationSpec,
            label = "StageStepBar progress animation",
        )
    }

    MainCanvas(
        modifier = modifier,
        progress = currentProgress.value,
        stageStepConfig = stageStepConfig.toImmutableList(),
        currentState = currentState,
        orientation = orientation,
        horizontalDirection = horizontalDirection,
        verticalDirection = verticalDirection,
        filledTrack = filledTrack,
        unfilledTrack = unfilledTrack,
        filledThumb = filledThumb,
        unfilledThumb = unfilledThumb,
        thumbSize = thumbSize,
        crossAxisSizeFilledTrack = crossAxisSizeFilledTrack,
        crossAxisSizeUnfilledTrack = crossAxisSizeUnfilledTrack,
        showThumbs = showThumbs,
        drawTracksBehindThumbs = drawTracksBehindThumbs,
    )
}

@Keep
@Composable
fun StageStepBar(
    modifier: Modifier = Modifier,
    config: StageStepBarConfig,
) {
    StageStepBar(
        modifier = modifier,
        stageStepConfig = config.stageStepConfig,
        currentState = config.currentState,
        shouldAnimate = config.shouldAnimate,
        animationSpec = config.animationSpec,
        orientation = config.orientation,
        horizontalDirection = config.horizontalDirection,
        verticalDirection = config.verticalDirection,
        filledTrack = config.filledTrack,
        unfilledTrack = config.unfilledTrack,
        filledThumb = config.filledThumb,
        unfilledThumb = config.unfilledThumb,
        thumbSize = config.thumbSize,
        crossAxisSizeFilledTrack = config.crossAxisSizeFilledTrack,
        crossAxisSizeUnfilledTrack = config.crossAxisSizeUnfilledTrack,
        showThumbs = config.showThumbs,
        drawTracksBehindThumbs = config.drawTracksBehindThumbs,
    )
}

private fun validateInput(
    stageStepConfig: List<Int>,
    currentState: State?,
    filledThumb: ThumbConfig,
    unfilledThumb: ThumbConfig,
) {
    if (stageStepConfig.isEmpty()) {
        throw IllegalArgumentException("The stageStepConfig should have at least one element.")
    }

    if (stageStepConfig.any { it <= 0 }) {
        throw IllegalArgumentException("The stageStepConfig should only include positive integers.")
    }

    if (currentState != null && (currentState.stage < 0 || currentState.step < 0)) {
        throw IllegalArgumentException("The state should only contain positive integers.")
    }

    if (filledThumb.drawnComponentList.isEmpty()) {
        throw IllegalStateException("The filledThumb list should have at least one DrawnComponent")
    }

    if (unfilledThumb.drawnComponentList.isEmpty()) {
        throw IllegalStateException("The unfilledThumb list should have at least one DrawnComponent")
    }
}

private fun calculateProgress(stageStepConfig: List<Int>, currentState: State?): Float {
    val numOfStages = stageStepConfig.size + 1
    return currentState?.let {
        val stage = it.stage.coerceAtMost(stageStepConfig.size - 1)
        val step = if (it.stage > stageStepConfig.size - 1) {
            0
        } else {
            it.step.coerceAtMost(stageStepConfig[stage])
        }

        if (it.stage >= stageStepConfig.size) {
            1f
        } else {
            stage.toFloat() / (numOfStages - 1) +
                    (step.toFloat() / (stageStepConfig[stage])) / (numOfStages - 1)
        }
    } ?: 0f
}

@Composable
private fun MainCanvas(
    modifier: Modifier,
    progress: Float,
    stageStepConfig: ImmutableList<Int>,
    currentState: State?,
    orientation: Orientation,
    horizontalDirection: HorizontalDirection,
    verticalDirection: VerticalDirection,
    filledTrack: DrawnComponent,
    unfilledTrack: DrawnComponent,
    filledThumb: ThumbConfig,
    unfilledThumb: ThumbConfig,
    thumbSize: Dp,
    crossAxisSizeFilledTrack: Dp,
    crossAxisSizeUnfilledTrack: Dp,
    showThumbs: Boolean,
    drawTracksBehindThumbs: Boolean,
) {
    val (thumbSizePx, crossAxisFilledTrackSizePx, crossAxisUnfilledTrackSizePx) =
        with(LocalDensity.current) {
            Triple(
                thumbSize.toPx(),
                crossAxisSizeFilledTrack.toPx(),
                crossAxisSizeUnfilledTrack.toPx()
            )
        }

    val canvasModifier = if (!drawTracksBehindThumbs) {
        modifier.drawOffscreen()
    } else {
        modifier
    }

    val internalFilledTrack = filledTrack.toInternal()
    val internalUnfilledTrack = unfilledTrack.toInternal()
    val internalFilledThumb = filledThumb.drawnComponentList.map { it.toInternal() }
    val internalUnfilledThumb = unfilledThumb.drawnComponentList.map { it.toInternal() }

    Canvas(modifier = canvasModifier, onDraw = {
        val drawReverse = (orientation == Orientation.Horizontal &&
                horizontalDirection == HorizontalDirection.Rtl) ||
                (orientation == Orientation.Vertical &&
                        verticalDirection == VerticalDirection.Btt) ||
                (orientation == Orientation.Horizontal &&
                        horizontalDirection == HorizontalDirection.Auto &&
                        layoutDirection == LayoutDirection.Rtl)

        drawUnfilledTrack(
            drawScope = this,
            orientation = orientation,
            unfilledTrack = internalUnfilledTrack,
            unfilledTrackSizePx = crossAxisUnfilledTrackSizePx,
            thumbSizePx = thumbSizePx,
        )

        val numOfStages = stageStepConfig.size + 1
        if (numOfStages > 1) {
            val progressBarEnd = drawFilledTrack(
                drawScope = this,
                drawReverse = drawReverse,
                progress = progress,
                orientation = orientation,
                filledTrack = internalFilledTrack,
                filledTrackSizePx = crossAxisFilledTrackSizePx,
                thumbSizePx = thumbSizePx,
            )

            if (showThumbs) {
                drawThumbs(
                    drawScope = this,
                    progress = progress,
                    progressBarEnd = progressBarEnd,
                    drawReverse = drawReverse,
                    numOfStages = numOfStages,
                    orientation = orientation,
                    drawTracksBehindThumbs = drawTracksBehindThumbs,
                    currentState = currentState,
                    filledThumbs = internalFilledThumb,
                    unfilledThumbs = internalUnfilledThumb,
                    thumbSizePx = thumbSizePx
                )
            }
        }
    })
}

@Composable
private fun DrawnComponent.toInternal(): InternalDrawnComponent {
    val context = LocalContext.current
    return when (this) {
        is DrawnComponent.Default -> InternalDrawnComponent.Default(this.color)
        is DrawnComponent.Drawable -> {
            val bitmap = remember(this.drawableRes) {
                val drawable = ContextCompat.getDrawable(context, this.drawableRes)

                val (width, height) = if (this.size != null) {
                    this.size.width to this.size.height
                } else {
                    drawable!!.intrinsicWidth to drawable.intrinsicHeight
                }
                drawable?.toBitmap(width, height)?.asImageBitmap()

            }
            InternalDrawnComponent.Bitmap(
                imageBitmap = bitmap!!,
                colorFilter = this.colorFilter,
                alpha = this.alpha,
            )
        }

        is DrawnComponent.Bitmap -> {
            InternalDrawnComponent.Bitmap(
                imageBitmap = this.imageBitmap,
                colorFilter = this.colorFilter,
                alpha = this.alpha,
            )
        }
    }
}

private fun drawUnfilledTrack(
    drawScope: DrawScope,
    unfilledTrack: InternalDrawnComponent,
    orientation: Orientation,
    unfilledTrackSizePx: Float,
    thumbSizePx: Float,
) {
    val left: Float
    val top: Float
    val right: Float
    val bottom: Float
    val canvasHeight = drawScope.size.height
    val canvasWidth = drawScope.size.width

    when (orientation) {
        Orientation.Horizontal -> {
            left = thumbSizePx / 2
            top = canvasHeight / 2 - unfilledTrackSizePx / 2
            right = canvasWidth - thumbSizePx / 2
            bottom = canvasHeight / 2 + unfilledTrackSizePx / 2
        }

        Orientation.Vertical -> {
            left = canvasWidth / 2 - unfilledTrackSizePx / 2
            top = thumbSizePx / 2
            right = canvasWidth / 2 + unfilledTrackSizePx / 2
            bottom = canvasHeight - thumbSizePx / 2
        }
    }

    when (unfilledTrack) {
        is InternalDrawnComponent.Default -> {
            drawScope.drawRect(
                color = unfilledTrack.color,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top)
            )
        }

        is InternalDrawnComponent.Bitmap -> {
            val imageBitmap = unfilledTrack.imageBitmap
            val colorFilter = unfilledTrack.colorFilter
            val alpha = unfilledTrack.alpha

            drawScope.drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(left.toInt(), top.toInt()),
                dstSize = IntSize((right - left).toInt(), (bottom - top).toInt()),
                colorFilter = colorFilter,
                alpha = alpha,
            )
        }
    }
}

private fun drawFilledTrack(
    drawScope: DrawScope,
    drawReverse: Boolean,
    progress: Float,
    orientation: Orientation,
    filledTrack: InternalDrawnComponent,
    filledTrackSizePx: Float,
    thumbSizePx: Float,
): Float {
    var left: Float
    var top: Float
    var right: Float
    var bottom: Float
    val progressBarEnd: Float
    val canvasHeight = drawScope.size.height
    val canvasWidth = drawScope.size.width

    when (orientation) {
        Orientation.Horizontal -> {
            top = canvasHeight / 2f - filledTrackSizePx / 2f
            bottom = canvasHeight / 2f + filledTrackSizePx / 2f
            left = thumbSizePx / 2f
            right =
                (canvasWidth - thumbSizePx) * progress + thumbSizePx / 2f

            if (drawReverse) {
                val temp = left
                left = canvasWidth - right
                right = canvasWidth - temp
            }
            progressBarEnd = if (drawReverse) left else right
        }

        Orientation.Vertical -> {
            left = canvasWidth / 2f - filledTrackSizePx / 2f
            right = canvasWidth / 2f + filledTrackSizePx / 2f
            top = thumbSizePx / 2f
            bottom =
                (canvasHeight - thumbSizePx) * progress + thumbSizePx / 2f

            if (drawReverse) {
                val temp = top
                top = canvasHeight - bottom
                bottom = canvasHeight - temp
            }
            progressBarEnd = if (drawReverse) top else bottom
        }
    }

    when (filledTrack) {
        is InternalDrawnComponent.Default -> {
            drawScope.drawRect(
                color = filledTrack.color,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top)
            )
        }

        is InternalDrawnComponent.Bitmap -> {
            val imageBitmap = filledTrack.imageBitmap
            val colorFilter = filledTrack.colorFilter
            val alpha = filledTrack.alpha

            drawScope.drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(left.toInt(), top.toInt()),
                dstSize = IntSize((right - left).toInt(), (bottom - top).toInt()),
                colorFilter = colorFilter,
                alpha = alpha,
            )
        }
    }

    return progressBarEnd
}

private fun List<InternalDrawnComponent>.getThumbAtStage(stage: Int): InternalDrawnComponent {
    return if (stage > size - 1) {
        this[size - 1]
    } else {
        this[stage]
    }
}

private fun drawThumbs(
    drawScope: DrawScope,
    progress: Float,
    progressBarEnd: Float,
    drawReverse: Boolean,
    numOfStages: Int,
    orientation: Orientation,
    drawTracksBehindThumbs: Boolean,
    currentState: State?,
    filledThumbs: List<InternalDrawnComponent>,
    unfilledThumbs: List<InternalDrawnComponent>,
    thumbSizePx: Float,
) {
    val canvasHeight = drawScope.size.height
    val canvasWidth = drawScope.size.width

    repeat(numOfStages) { stage ->
        val filledThumb = filledThumbs.getThumbAtStage(stage)
        val unfilledThumb = unfilledThumbs.getThumbAtStage(stage)

        val canvasLimit = if (orientation == Orientation.Horizontal) {
            canvasWidth
        } else {
            canvasHeight
        }
        var centerOfThisThumb =
            (stage * (canvasLimit - thumbSizePx) / (numOfStages - 1).toFloat() + thumbSizePx / 2f).toInt()

        if (drawReverse) {
            centerOfThisThumb = canvasLimit.toInt() - centerOfThisThumb
        }

        val shouldFillThumb = when {
            currentState == null && stage == 0 && progress == 0f -> false
            drawReverse -> progressBarEnd <= centerOfThisThumb
            else -> progressBarEnd >= centerOfThisThumb
        }

        when (val drawnComponent =
            if (shouldFillThumb) filledThumb else unfilledThumb) {
            is InternalDrawnComponent.Default -> {
                when (orientation) {
                    Orientation.Horizontal -> {
                        if (!drawTracksBehindThumbs) {
                            drawScope.drawCircle(
                                color = Color.Transparent,
                                center = Offset(
                                    centerOfThisThumb.toFloat(),
                                    canvasHeight / 2f
                                ),
                                radius = thumbSizePx / 2f,
                                blendMode = BlendMode.Clear
                            )
                        }

                        drawScope.drawCircle(
                            color = drawnComponent.color,
                            center = Offset(
                                centerOfThisThumb.toFloat(),
                                canvasHeight / 2f
                            ),
                            radius = thumbSizePx / 2f,
                        )
                    }

                    Orientation.Vertical -> {
                        if (!drawTracksBehindThumbs) {
                            drawScope.drawCircle(
                                color = Color.Transparent,
                                center = Offset(
                                    canvasWidth / 2f,
                                    centerOfThisThumb.toFloat(),
                                ),
                                radius = thumbSizePx / 2f,
                                blendMode = BlendMode.Clear
                            )
                        }

                        drawScope.drawCircle(
                            color = drawnComponent.color,
                            center = Offset(
                                canvasWidth / 2f,
                                centerOfThisThumb.toFloat(),
                            ),
                            radius = thumbSizePx / 2f,
                        )
                    }
                }
            }

            is InternalDrawnComponent.Bitmap -> {
                val imageBitmap = drawnComponent.imageBitmap
                val colorFilter = drawnComponent.colorFilter

                val left: Int
                val top: Int
                val right: Int
                val bottom: Int

                when (orientation) {
                    Orientation.Horizontal -> {
                        left = centerOfThisThumb - (thumbSizePx / 2f).toInt()
                        top = (canvasHeight / 2f).toInt() - (thumbSizePx / 2f).toInt()
                        right = centerOfThisThumb + (thumbSizePx / 2f).toInt()
                        bottom = (canvasHeight / 2f).toInt() + (thumbSizePx / 2f).toInt()
                    }

                    Orientation.Vertical -> {
                        left = (canvasWidth / 2f).toInt() - (thumbSizePx / 2f).toInt()
                        top = centerOfThisThumb - (thumbSizePx / 2f).toInt()
                        right = (canvasWidth / 2f).toInt() + (thumbSizePx / 2f).toInt()
                        bottom = centerOfThisThumb + (thumbSizePx / 2f).toInt()
                    }
                }

                if (!drawTracksBehindThumbs) {
                    drawScope.drawRect(
                        color = Color.Transparent,
                        topLeft = Offset(left.toFloat(), top.toFloat()),
                        size = Size((right - left).toFloat(), (bottom - top).toFloat()),
                        blendMode = BlendMode.Clear
                    )
                }

                drawScope.drawImage(
                    image = imageBitmap,
                    dstOffset = IntOffset(left, top),
                    dstSize = IntSize(right - left, bottom - top),
                    colorFilter = colorFilter,
                    alpha = drawnComponent.alpha,
                )
            }
        }
    }
}

@Preview(name = "StageStepBar Preview")
@Composable
private fun Preview() {
    // Change the layoutDirection here to force LTR or RTL
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        StageStepBar(
            modifier = Modifier
                .height(50.dp)
                .width(150.dp),
            stageStepConfig = listOf(5, 5, 5),
            currentState = State(2, 3),
        )
    }
}

private fun Modifier.drawOffscreen(): Modifier = this.drawWithContent {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        drawContent()
        restoreToCount(checkPoint)
    }
}

private sealed class InternalDrawnComponent {
    @Immutable // ImageBitmap is an interface (does not mutate)
    data class Bitmap(
        val imageBitmap: ImageBitmap,
        val colorFilter: ColorFilter? = null,
        val alpha: Float = 1f,
    ) : InternalDrawnComponent()

    data class Default(
        val color: Color,
    ) : InternalDrawnComponent()
}