package com.loukwn.stagestepbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.loukwn.stagestepbar.util.ConfigBuilder
import com.loukwn.stagestepbar.util.StageStepBarLifecycleObserver
import java.lang.IllegalArgumentException
import kotlin.math.round

@Keep
class StageStepBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var filledTrackPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
    }
    private var unfilledTrackPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
    }
    private var filledThumbPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
    }
    private var unfilledThumbPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val numOfStages: Int
        get() = config.stageStepConfig.size + 1

    private var currentProgress = 0f
    private var animatedProgress = 0f

    private var unfilledTrackRect = Rect()
    private var filledTrackRect = Rect()

    private var config: StageStepBarConfig = getDefaultConfig()

    private val isRtl by lazy { resources.getBoolean(R.bool.isRTL) }

    private var progressAnimator: ValueAnimator? = null
    private var isAnimating = false

    private lateinit var bitmap: Bitmap
    private lateinit var transparentCanvas: Canvas

    private val lifecycleObserver by lazy { StageStepBarLifecycleObserver() }

    private val clearPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            style = Paint.Style.FILL
            color = Color.TRANSPARENT
        }
    }

    init {
        attrs?.let { parseAttributes(it) }
        initializeLifecycleObserver()
        currentProgress = calculateNewProgress()
        animatedProgress = currentProgress
        updatePaintColors()
    }

    private fun initializeLifecycleObserver() {
        (context as? LifecycleOwner)?.let {
            lifecycleObserver.bindToLifecycleOwner(
                lifecycleOwner = it,
                doOnStart = ::onLifecycleStart,
                doOnStop = ::onLifecycleStop,
            )
        }
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.StageStepBar)
        try {
            config = ConfigBuilder().getFromAttributes(
                context = context,
                attrArray = a,
                oldConfig = config
            )
        } finally {
            a.recycle()
        }
    }

    private fun updatePaintColors() {
        if (config.filledThumb is DrawnComponent.Default) {
            val color = (config.filledThumb as DrawnComponent.Default).color
            filledThumbPaint.color = color
        }

        if (config.unfilledThumb is DrawnComponent.Default) {
            val color = (config.unfilledThumb as DrawnComponent.Default).color
            unfilledThumbPaint.color = color
        }

        if (config.filledTrack is DrawnComponent.Default) {
            val color = (config.filledTrack as DrawnComponent.Default).color
            filledTrackPaint.color = color
        }

        if (config.unfilledTrack is DrawnComponent.Default) {
            val color = (config.unfilledTrack as DrawnComponent.Default).color
            unfilledTrackPaint.color = color
        }
    }

    /**
     * This sets the steps per stages configuration so that the StageStepBar knows how to draw its
     * regions.
     *
     * @param stepsPerStages List that should have the same number of elements as the stages (or dots)
     * that we want and each element on the list should represent how many steps are needed to go to
     * the next stage. Only positive nonzero integers are allowed in the list.
     *
     * e.g [1,2,5] means 1 step to go from stage 0 to stage 1, 2 steps to go from stage 1 to stage 2
     * etc.
     */
    fun setStageStepConfig(stepsPerStages: List<Int>) {
        if (config.stageStepConfig != stepsPerStages) {
            config = when {
                stepsPerStages.isEmpty() -> throw IllegalArgumentException(
                    "The stageStepConfig should have at least one element."
                )
                stepsPerStages.any { it <= 0 } -> throw IllegalArgumentException(
                    "The stageStepConfig should only include positive integers."
                )
                else -> config.copy(stageStepConfig = stepsPerStages)
            }
            redraw()
        }
    }

    /**
     * This sets the current state of the progress bar.
     *
     * @param state A nullable State object. If it is null it means that the StageStepBar is
     * totally unfilled (not even first dot/stage is done). If it is not null then the appropriate
     * stage and step are filled.
     *
     * If the stage or step in stage provided by user is bigger than it is supposed to be
     * (see [setStageStepConfig]) then the largest possible value is chosen.
     *
     * e.g: Assuming [2,2,5] is the stageStepConfig
     * State(0, 10) will turn into State(0, 2)
     * State(3, 2) will turn into State(3, 0)
     * State(12, 2) will turn into State(3, 0)
     */
    fun setCurrentState(state: State?) {
        if (config.currentState != state) {
            if (state != null && (state.stage < 0 || state.step < 0)) {
                throw IllegalArgumentException("The state should only contain positive integers.")
            }
            config = config.copy(currentState = state)
            redraw()
        }
    }

    /**
     * Sets whether the changes in the StageStepBar should be done with an animation. This does not
     * cause a redraw.
     */
    fun setAnimate(animate: Boolean) {
        if (config.shouldAnimate != animate) {
            config = config.copy(shouldAnimate = animate)
        }
    }

    /**
     * Sets the animation duration of the StageStepBar. This does not cause a redraw.
     *
     * @param animationDuration The duration in ms.
     */
    fun setAnimationDuration(animationDuration: Long) {
        if (config.animationDuration != animationDuration) {
            config = config.copy(animationDuration = animationDuration)
        }
    }

    /**
     * Sets whether the StageStepBar will be drawn in an horizontal or vertical manner.
     *
     * @param orientation Can be either Horizontal or Vertical.
     */
    fun setOrientation(orientation: Orientation) {
        if (config.orientation != orientation) {
            config = config.copy(orientation = orientation)
            redraw()
        }
    }

    /**
     * Sets the direction in which a StageStepBar with an Horizontal Orientation will animate its
     * progress.
     *
     * @param horizontalDirection Can be left to right (Ltr), right to left (Rtl) or Auto.
     * Auto means that the orientation will be set based on the device locale/layoutDirection.
     */
    fun setHorizontalDirection(horizontalDirection: HorizontalDirection) {
        if (config.horizontalDirection != horizontalDirection) {
            config = config.copy(horizontalDirection = horizontalDirection)
            redraw()
        }
    }

    /**
     * Sets the direction in which a StageStepBar with an Vertical Orientation will animate its
     * progress.
     *
     * @param verticalDirection Can be top to bottom (Ttb) or bottom to top (Btt).
     */
    fun setVerticalDirection(verticalDirection: VerticalDirection) {
        if (config.verticalDirection != verticalDirection) {
            config = config.copy(verticalDirection = verticalDirection)
            redraw()
        }
    }

    /**
     * Sets (or reverts) the filled track to its default shape (rectangle).
     *
     * @param filledTrackColor Color for the default shape.
     */
    fun setFilledTrackToNormalShape(@ColorInt filledTrackColor: Int) {
        val newDrawnComponent = DrawnComponent.Default(filledTrackColor)
        if (config.filledTrack != newDrawnComponent) {
            config = config.copy(filledTrack = newDrawnComponent)
            updatePaintColors()
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets the filled track to be a user specified drawable.
     *
     * @param filledTrackDrawable The drawable to set on the filled track.
     * @param alpha Alpha for the drawable.
     */
    fun setFilledTrackToCustomDrawable(filledTrackDrawable: Drawable, alpha: Float = 1f) {
        val newDrawnComponent = DrawnComponent.UserProvided(filledTrackDrawable, alpha)
        if (config.filledTrack != newDrawnComponent) {
            config = config.copy(filledTrack = newDrawnComponent)
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets (or reverts) the unfilled track to its default shape (rectangle).
     *
     * @param unfilledTrackColor Color for the default shape.
     */
    fun setUnfilledTrackToNormalShape(@ColorInt unfilledTrackColor: Int) {
        val newDrawnComponent = DrawnComponent.Default(unfilledTrackColor)
        if (config.unfilledTrack != newDrawnComponent) {
            config = config.copy(unfilledTrack = newDrawnComponent)
            updatePaintColors()
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets the unfilled track to be a user specified drawable.
     *
     * @param unfilledTrackDrawable The drawable to set on the filled track.
     * @param alpha Alpha for the drawable.
     */
    fun setUnfilledTrackToCustomDrawable(unfilledTrackDrawable: Drawable, alpha: Float = 1f) {
        val newDrawnComponent = DrawnComponent.UserProvided(unfilledTrackDrawable, alpha)
        if (config.unfilledTrack != newDrawnComponent) {
            config = config.copy(unfilledTrack = newDrawnComponent)
            redraw()
        }
    }

    /**
     * Sets (or reverts) the filled thumb to its default shape (rectangle).
     *
     * @param filledThumbColor Color for the default shape.
     */
    fun setFilledThumbToNormalShape(@ColorInt filledThumbColor: Int) {
        val newDrawnComponent = DrawnComponent.Default(filledThumbColor)
        if (config.filledThumb != newDrawnComponent) {
            config = config.copy(filledThumb = newDrawnComponent)
            updatePaintColors()
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets the filled thumb to be a user specified drawable.
     *
     * @param filledThumbDrawable The drawable to set on the filled thumb.
     * @param alpha Alpha for the drawable.
     */
    fun setFilledThumbToCustomDrawable(filledThumbDrawable: Drawable, alpha: Float = 1f) {
        val newDrawnComponent = DrawnComponent.UserProvided(filledThumbDrawable, alpha)
        if (config.filledThumb != newDrawnComponent) {
            config = config.copy(filledThumb = newDrawnComponent)
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets the filled thumb to be a user specified drawable.
     *
     * @param filledThumbDrawable The drawable to set on the filled thumb.
     * @param alpha Alpha for the drawable.
     */
    fun setFilledDoneThumbToCustomDrawable(filledThumbDrawable: Drawable, alpha: Float = 1f) {
        val newDrawnComponent = DrawnComponent.UserProvided(filledThumbDrawable, alpha)
        if (config.filledDoneThumb != newDrawnComponent) {
            config = config.copy(filledDoneThumb = newDrawnComponent)
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets (or reverts) the unfilled thumb to its default shape (rectangle).
     *
     * @param unfilledThumbColor Color for the default shape
     */
    fun setUnfilledThumbToNormalShape(@ColorInt unfilledThumbColor: Int) {
        val newDrawnComponent = DrawnComponent.Default(unfilledThumbColor)
        if (config.unfilledThumb != newDrawnComponent) {
            config = config.copy(unfilledThumb = newDrawnComponent)
            updatePaintColors()
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets the unfilled thumb to be a user specified drawable.
     *
     * @param unfilledThumbDrawable The drawable to set on the filled thumb.
     * @param alpha Alpha for the drawable.
     */
    fun setUnfilledThumbToCustomDrawable(unfilledThumbDrawable: Drawable, alpha: Float = 1f) {
        val newDrawnComponent = DrawnComponent.UserProvided(unfilledThumbDrawable, alpha)
        if (config.unfilledThumb != newDrawnComponent) {
            config = config.copy(unfilledThumb = newDrawnComponent)
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets the size of the thumb. This will actually set both its width and height to be this value.
     * Both the default shape and a user specified drawable will share this thumbSize.
     *
     * @param thumbSize The desired size in px.
     */
    fun setThumbSize(thumbSize: Int) {
        if (config.thumbSize != thumbSize) {
            config = config.copy(thumbSize = thumbSize)
            redraw()
        }
    }

    /**
     * Sets the size of the filled track on the cross axis of the StageStepBar orientation.
     * e.g If the orientation is vertical then this will set the track width and vice versa.
     *
     * @param size The desired size in px.
     */
    fun setCrossAxisFilledTrackSize(size: Int) {
        if (config.crossAxisSizeFilledTrack != size) {
            config = config.copy(crossAxisSizeFilledTrack = size)
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets the size of the unfilled track on the cross axis of the StageStepBar orientation.
     * e.g if the orientation is vertical then this will set the track width and vice versa.
     *
     * @param size The desired size in px.
     */
    fun setCrossAxisUnfilledTrackSize(size: Int) {
        if (config.crossAxisSizeUnfilledTrack != size) {
            config = config.copy(crossAxisSizeUnfilledTrack = size)
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets whether the thumbs should be drawn or not.
     */
    fun setThumbsVisible(visible: Boolean) {
        if (config.showThumbs != visible) {
            config = config.copy(showThumbs = visible)
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    /**
     * Sets whether the tracks are visible behind the thumbs. This is by default set to true, as it
     * requires one extra draw (erasing the area behind the thumb) otherwise.
     * This however can be particularly useful if semitransparent thumbs are used and seeing
     * the track peeking through them is not desired.
     */
    fun setDrawTracksBehindThumbs(drawTracks: Boolean) {
        if (config.drawTracksBehindThumbs != drawTracks) {
            config = config.copy(drawTracksBehindThumbs = drawTracks)
            if (!isCurrentlyAnimating()) {
                redraw()
            }
        }
    }

    private fun onLifecycleStart() {
        if (animatedProgress != currentProgress) {
            redraw()
        } else {
            invalidate()
        }
    }

    private fun onLifecycleStop() {
        progressAnimator?.cancel()
        progressAnimator = null
    }

    override fun onDetachedFromWindow() {
        progressAnimator?.cancel()
        progressAnimator = null
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isAnimating) {
            redraw()
        }
    }

    private fun calculateNewProgress(): Float =

        config.currentState?.let {
            val stage = it.stage.coerceAtMost(config.stageStepConfig.size - 1)
            val step = if (it.stage > config.stageStepConfig.size - 1) {
                0
            } else {
                it.step.coerceAtMost(config.stageStepConfig[stage])
            }

            if (it.stage >= config.stageStepConfig.size) {
                1f
            } else {
                stage.toFloat() / (numOfStages - 1) +
                        (step.toFloat() / (config.stageStepConfig[stage])) / (numOfStages - 1)
            }
        } ?: 0f

    private fun animatedInvalidation() {
        val currentAnimatedProgress = animatedProgress
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofFloat(currentAnimatedProgress, currentProgress).apply {
            addUpdateListener {
                animatedProgress = it.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    isAnimating = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    isAnimating = false
                }
            })
            duration = config.animationDuration
        }
        progressAnimator?.start()
    }

    private fun isCurrentlyAnimating(): Boolean = isAnimating

    private fun redraw() {
        currentProgress = calculateNewProgress()

        if (config.shouldAnimate) {
            animatedInvalidation()
        } else {
            progressAnimator?.cancel()
            progressAnimator = null
            isAnimating = false

            animatedProgress = currentProgress
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {

        val drawReverse = (config.orientation == Orientation.Horizontal &&
                config.horizontalDirection == HorizontalDirection.Rtl) ||
                (config.orientation == Orientation.Vertical &&
                        config.verticalDirection == VerticalDirection.Btt) ||
                (config.orientation == Orientation.Horizontal &&
                        config.horizontalDirection == HorizontalDirection.Auto &&
                        isRtl)

        val canvasToUse = transparentCanvas

        // This deletes any artifacts from previous draws.
        canvasToUse.drawRect(0f, 0f, width.toFloat(), height.toFloat(), clearPaint)

        if (numOfStages > 1) {
            val progressBarEnd = drawFilledTrack(
                canvas = canvasToUse,
                drawReverse = drawReverse
            )

            val startOfUnfilledTrack = if (progressBarEnd == 0) {
                when (config.orientation) {
                    Orientation.Horizontal -> {
                        if (drawReverse) width - config.thumbSize / 2f else config.thumbSize / 2f
                    }
                    Orientation.Vertical -> {
                        if (drawReverse) height - config.thumbSize / 2f else config.thumbSize / 2f
                    }
                }
            } else {
                progressBarEnd
            }

            drawUnfilledTrack(
                canvas = canvasToUse,
                endOfFilledTrack = startOfUnfilledTrack.toInt(),
                drawReverse = drawReverse
            )

            if (config.showThumbs) {
                drawThumbs(
                    canvas = canvasToUse,
                    progressBarEnd = progressBarEnd,
                    drawReverse = drawReverse
                )
            }
        } else {
            drawUnfilledTrack(
                canvas = canvasToUse,
                endOfFilledTrack = (config.thumbSize / 2f).toInt(),
                drawReverse = drawReverse
            )
        }

        // Draw the bitmap from the transparentCanvas to the canvas of the View.
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    private fun drawUnfilledTrack(canvas: Canvas, endOfFilledTrack: Int, drawReverse: Boolean) {
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int

        when (config.orientation) {
            Orientation.Horizontal -> {
                left = if (drawReverse) {
                    (config.thumbSize / 2f).toInt()
                } else {
                    endOfFilledTrack
                }
                top = (canvas.height / 2f - config.crossAxisSizeUnfilledTrack / 2f).toInt()
                right = if (drawReverse) {
                    endOfFilledTrack
                } else {
                    (canvas.width - config.thumbSize / 2f).toInt()
                }
                bottom = (canvas.height / 2f + config.crossAxisSizeUnfilledTrack / 2f).toInt()
            }
            Orientation.Vertical -> {
                left = (canvas.width / 2f - config.crossAxisSizeUnfilledTrack / 2f).toInt()
                top = if (drawReverse) {
                    (config.thumbSize / 2f).toInt()
                } else {
                    endOfFilledTrack
                }
                right = (canvas.width / 2f + config.crossAxisSizeUnfilledTrack / 2f).toInt()
                bottom = if (drawReverse) {
                    endOfFilledTrack
                } else {
                    (canvas.height - config.thumbSize / 2f).toInt()
                }
            }
        }

        when (config.unfilledTrack) {
            is DrawnComponent.Default -> {
                unfilledTrackRect.set(left, top, right, bottom)
                canvas.drawRect(unfilledTrackRect, clearPaint)
                canvas.drawRect(unfilledTrackRect, unfilledTrackPaint)
            }
            is DrawnComponent.UserProvided -> {
                val (drawable, alpha) = with(config.unfilledTrack as DrawnComponent.UserProvided) {
                    drawable to round(255 * alpha).toInt()
                }
                drawable.alpha = alpha
                canvas.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    clearPaint
                )
                drawable.setBounds(left, top, right, bottom)
                drawable.draw(canvas)
            }
        }
    }

    private fun drawFilledTrack(canvas: Canvas, drawReverse: Boolean): Int {
        var left: Int
        var top: Int
        var right: Int
        var bottom: Int
        val progressBarEnd: Int

        when (config.orientation) {
            Orientation.Horizontal -> {
                top = (canvas.height / 2f - config.crossAxisSizeFilledTrack / 2f).toInt()
                bottom = (canvas.height / 2f + config.crossAxisSizeFilledTrack / 2f).toInt()
                left = (config.thumbSize / 2f).toInt()
                right =
                    ((canvas.width - config.thumbSize) * animatedProgress + config.thumbSize / 2f).toInt()

                if (drawReverse) {
                    val temp = left
                    left = canvas.width - right
                    right = canvas.width - temp
                }
                progressBarEnd = if (drawReverse) left else right
            }
            Orientation.Vertical -> {
                left = (canvas.width / 2f - config.crossAxisSizeFilledTrack / 2f).toInt()
                right = (canvas.width / 2f + config.crossAxisSizeFilledTrack / 2f).toInt()
                top = (config.thumbSize / 2f).toInt()
                bottom =
                    ((canvas.height - config.thumbSize) * animatedProgress + config.thumbSize / 2f).toInt()

                if (drawReverse) {
                    val temp = top
                    top = canvas.height - bottom
                    bottom = canvas.height - temp
                }
                progressBarEnd = if (drawReverse) top else bottom
            }
        }

        when (config.filledTrack) {
            is DrawnComponent.Default -> {
                filledTrackRect.set(left, top, right, bottom)
                canvas.drawRect(filledTrackRect, clearPaint)
                canvas.drawRect(filledTrackRect, filledTrackPaint)
            }
            is DrawnComponent.UserProvided -> {
                val (drawable, alpha) = with(config.filledTrack as DrawnComponent.UserProvided) {
                    drawable to round(255 * alpha).toInt()
                }
                drawable.alpha = alpha
                canvas.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    clearPaint
                )
                drawable.setBounds(left, top, right, bottom)
                drawable.draw(canvas)
            }
        }

        return progressBarEnd
    }

    private fun drawThumbs(canvas: Canvas, progressBarEnd: Int, drawReverse: Boolean) {
        repeat(numOfStages) { stage ->

            val canvasLimit = if (config.orientation == Orientation.Horizontal) {
                canvas.width
            } else {
                canvas.height
            }
            var centerOfThisThumb =
                (stage * (canvasLimit - config.thumbSize) / (numOfStages - 1).toFloat() + config.thumbSize / 2f).toInt()

            if (drawReverse) {
                centerOfThisThumb = canvasLimit - centerOfThisThumb
            }

            val shouldFillThumb = when {
                config.currentState == null && animatedProgress == currentProgress -> false
                drawReverse -> progressBarEnd <= centerOfThisThumb
                else -> progressBarEnd >= centerOfThisThumb
            }

            val shouldShowDone = if (shouldFillThumb) {
                progressBarEnd != centerOfThisThumb
            } else {
                false
            }

            val paint = if (shouldFillThumb) filledThumbPaint else unfilledThumbPaint

            when (val drawnComponent =
                if (shouldFillThumb) {
                    if (shouldShowDone) config.filledDoneThumb else config.filledThumb
                } else {
                    config.unfilledThumb
                }
            ) {
                is DrawnComponent.Default -> {
                    when (config.orientation) {
                        Orientation.Horizontal -> {
                            if (!config.drawTracksBehindThumbs) {
                                canvas.drawCircle(
                                    centerOfThisThumb.toFloat(),
                                    canvas.height / 2f,
                                    config.thumbSize / 2f,
                                    clearPaint
                                )
                            }
                            canvas.drawCircle(
                                centerOfThisThumb.toFloat(),
                                canvas.height / 2f,
                                config.thumbSize / 2f,
                                paint
                            )
                        }
                        Orientation.Vertical -> {
                            if (!config.drawTracksBehindThumbs) {
                                canvas.drawCircle(
                                    canvas.width / 2f,
                                    centerOfThisThumb.toFloat(),
                                    config.thumbSize / 2f,
                                    clearPaint
                                )
                            }
                            canvas.drawCircle(
                                canvas.width / 2f,
                                centerOfThisThumb.toFloat(),
                                config.thumbSize / 2f,
                                paint
                            )
                        }
                    }
                }
                is DrawnComponent.UserProvided -> {
                    val alpha = round(255 * drawnComponent.alpha).toInt()
                    val left: Int
                    val top: Int
                    val right: Int
                    val bottom: Int

                    when (config.orientation) {
                        Orientation.Horizontal -> {
                            left = centerOfThisThumb - (config.thumbSize / 2f).toInt()
                            top = (canvas.height / 2f).toInt() - (config.thumbSize / 2f).toInt()
                            right = centerOfThisThumb + (config.thumbSize / 2f).toInt()
                            bottom = (canvas.height / 2f).toInt() + (config.thumbSize / 2f).toInt()

                            if (!config.drawTracksBehindThumbs) {
                                canvas.drawRect(
                                    left.toFloat(),
                                    top.toFloat(),
                                    right.toFloat(),
                                    bottom.toFloat(),
                                    clearPaint
                                )
                            }
                            drawnComponent.drawable.setBounds(
                                left, top, right, bottom,
                            )
                            drawnComponent.drawable.alpha = alpha
                            drawnComponent.drawable.draw(canvas)
                        }
                        Orientation.Vertical -> {
                            left = (canvas.width / 2f).toInt() - (config.thumbSize / 2f).toInt()
                            top = centerOfThisThumb - (config.thumbSize / 2f).toInt()
                            right = (canvas.width / 2f).toInt() + (config.thumbSize / 2f).toInt()
                            bottom = centerOfThisThumb + (config.thumbSize / 2f).toInt()

                            if (!config.drawTracksBehindThumbs) {
                                canvas.drawRect(
                                    left.toFloat(),
                                    top.toFloat(),
                                    right.toFloat(),
                                    bottom.toFloat(),
                                    clearPaint
                                )
                            }

                            drawnComponent.drawable.setBounds(
                                left, top, right, bottom,
                            )
                            drawnComponent.drawable.alpha = alpha
                            drawnComponent.drawable.draw(canvas)
                        }
                    }
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.TRANSPARENT)
        transparentCanvas = Canvas(bitmap)
    }

    private fun getDefaultConfig(): StageStepBarConfig {
        return StageStepBarConfig(
            stageStepConfig = listOf(50, 50),
            currentState = null,
            shouldAnimate = true,
            animationDuration = 500L,
            orientation = Orientation.Horizontal,
            horizontalDirection = HorizontalDirection.Auto,
            verticalDirection = VerticalDirection.Btt,
            filledTrack = DrawnComponent.Default(
                color = ContextCompat.getColor(context, R.color.default_track_filled_color)
            ),
            unfilledTrack = DrawnComponent.Default(
                color = ContextCompat.getColor(context, R.color.default_track_unfilled_color)
            ),
            filledThumb = DrawnComponent.Default(
                color = ContextCompat.getColor(context, R.color.default_thumb_filled_color)
            ),
            filledDoneThumb = DrawnComponent.Default(
                color = ContextCompat.getColor(context, R.color.default_thumb_filled_color)
            ),
            unfilledThumb = DrawnComponent.Default(
                color = ContextCompat.getColor(context, R.color.default_thumb_unfilled_color)
            ),
            thumbSize = resources.getDimensionPixelSize(R.dimen.default_thumb_size),
            crossAxisSizeFilledTrack = resources.getDimensionPixelOffset(R.dimen.default_track_cross_axis_size),
            crossAxisSizeUnfilledTrack = resources.getDimensionPixelOffset(R.dimen.default_track_cross_axis_size),
            showThumbs = true,
            drawTracksBehindThumbs = true,
        )
    }

    internal data class StageStepBarConfig(
        val stageStepConfig: List<Int>,
        val currentState: State?,
        val shouldAnimate: Boolean,
        val animationDuration: Long,
        val orientation: Orientation,
        val horizontalDirection: HorizontalDirection,
        val verticalDirection: VerticalDirection,
        val filledTrack: DrawnComponent,
        val unfilledTrack: DrawnComponent,
        val filledThumb: DrawnComponent,
        val filledDoneThumb: DrawnComponent,
        val unfilledThumb: DrawnComponent,
        val thumbSize: Int,
        val crossAxisSizeFilledTrack: Int,
        val crossAxisSizeUnfilledTrack: Int,
        val showThumbs: Boolean,
        val drawTracksBehindThumbs: Boolean,
    )

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

    internal sealed class DrawnComponent {
        data class UserProvided(
            val drawable: Drawable,
            val alpha: Float = 1f,
        ) : DrawnComponent()

        data class Default(
            @ColorInt val color: Int,
        ) : DrawnComponent()
    }

    @Keep
    data class State(val stage: Int, val step: Int)
}