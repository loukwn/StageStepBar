package com.loukwn.stagestepbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.loukwn.stagestepbar.util.ConfigBuilder
import com.loukwn.stagestepbar.util.StageStepBarLifecycleObserver

@Keep
class StageStepBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr), StageStepBarLifecycleObserver.Listener {

    private val configBuilder: ConfigBuilder = ConfigBuilder()

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
    private val lifecycleObserver by lazy { StageStepBarLifecycleObserver() }

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
                listener = this
            )
        }
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.StageStepBar)
        try {
            config = configBuilder.getFromAttributes(
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
            config = configBuilder.getWithNewStageStepConfig(stepsPerStages, config)
            redraw()
        }
    }

    /**
     * This sets the current state of the progress bar.
     *
     * @param currentState A nullable State object. If it is null it means that the StageStepBar is
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
    fun setCurrentState(currentState: State?) {
        if (config.currentState != currentState) {
            config = configBuilder.getWithNewCurrentState(currentState, config)
            redraw()
        }
    }

    /**
     * Sets whether the changes in the StageStepBar should be done with an animation. This does not
     * cause a redraw.
     */
    fun setAnimate(animate: Boolean) {
        if (config.shouldAnimate != animate) {
            config = configBuilder.getWithAnimate(animate, config)
        }
    }

    /**
     * Sets the animation duration of the StageStepBar. This does not cause a redraw.
     */
    fun setAnimationDuration(animationDuration: Long) {
        if (config.animationDuration != animationDuration) {
            config = configBuilder.getWithAnimationDuration(animationDuration, config)
        }
    }

    /**
     * Sets whether the StageStepBar will be drawn in an horizontal or vertical manner.
     */
    fun setOrientation(orientation: Orientation) {
        if (config.orientation != orientation) {
            config = configBuilder.getWithOrientation(orientation, config)
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
            config = configBuilder.getWithHorizontalDirection(horizontalDirection, config)
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
            config = configBuilder.getWithVerticalDirection(verticalDirection, config)
            redraw()
        }
    }

    /**
     * Sets (or reverts) the filled track to its default shape (rectangle).
     *
     * @param filledTrackColor Color for the default shape.
     */
    fun setFilledTrackToNormalShape(@ColorInt filledTrackColor: Int) {
        if (config.filledTrack is DrawnComponent.UserProvided ||
            (config.filledTrack is DrawnComponent.Default &&
                    (config.filledTrack as DrawnComponent.Default).color != filledTrackColor)
        ) {

            config = configBuilder.getWithFilledTrackAsDefaultShape(filledTrackColor, config)
            updatePaintColors()
            redraw()
        }
    }

    /**
     * Sets the filled track to be a user specified drawable.
     */
    fun setFilledTrackToCustomDrawable(filledTrackDrawable: Drawable) {
        if (config.filledTrack is DrawnComponent.Default ||
            (config.filledTrack is DrawnComponent.UserProvided &&
                    (config.filledTrack as DrawnComponent.UserProvided).drawable != filledTrackDrawable)
        ) {

            config = configBuilder.getWithFilledTrackAsDrawable(filledTrackDrawable, config)
            redraw()
        }
    }

    /**
     * Sets (or reverts) the unfilled track to its default shape (rectangle).
     *
     * @param unfilledTrackColor Color for the default shape.
     */
    fun setUnfilledTrackToNormalShape(@ColorInt unfilledTrackColor: Int) {
        if (config.unfilledTrack is DrawnComponent.UserProvided ||
            (config.unfilledTrack is DrawnComponent.Default &&
                    (config.unfilledTrack as DrawnComponent.Default).color != unfilledTrackColor)
        ) {

            config = configBuilder.getWithUnfilledTrackAsDefaultShape(unfilledTrackColor, config)
            updatePaintColors()
            redraw()
        }
    }

    /**
     * Sets the unfilled track to be a user specified drawable.
     */
    fun setUnfilledTrackToCustomDrawable(unfilledTrackDrawable: Drawable) {
        if (config.unfilledTrack is DrawnComponent.Default ||
            (config.unfilledTrack is DrawnComponent.UserProvided &&
                    (config.unfilledTrack as DrawnComponent.UserProvided).drawable != unfilledTrackDrawable)
        ) {

            config = configBuilder.getWithUnfilledTrackAsDrawable(unfilledTrackDrawable, config)
            redraw()
        }
    }

    /**
     * Sets (or reverts) the filled thumb to its default shape (rectangle).
     *
     * @param filledThumbColor Color for the default shape.
     */
    fun setFilledThumbToNormalShape(@ColorInt filledThumbColor: Int) {
        if (config.filledThumb is DrawnComponent.UserProvided ||
            (config.filledThumb is DrawnComponent.Default &&
                    (config.filledThumb as DrawnComponent.Default).color != filledThumbColor)
        ) {

            config = configBuilder.getWithFilledThumbAsDefaultShape(filledThumbColor, config)
            updatePaintColors()
            redraw()
        }
    }

    /**
     * Sets the filled thumb to be a user specified drawable.
     */
    fun setFilledThumbToCustomDrawable(filledThumbDrawable: Drawable) {
        if (config.filledThumb is DrawnComponent.Default ||
            (config.filledThumb is DrawnComponent.UserProvided &&
                    (config.filledThumb as DrawnComponent.UserProvided).drawable != filledThumbDrawable)
        ) {

            config = configBuilder.getWithFilledThumbAsDrawable(filledThumbDrawable, config)
            redraw()
        }
    }

    /**
     * Sets (or reverts) the unfilled thumb to its default shape (rectangle).
     *
     * @param unfilledThumbColor Color for the default shape
     */
    fun setUnfilledThumbToNormalShape(@ColorInt unfilledThumbColor: Int) {
        if (config.unfilledThumb is DrawnComponent.UserProvided ||
            (config.unfilledThumb is DrawnComponent.Default &&
                    (config.unfilledThumb as DrawnComponent.Default).color != unfilledThumbColor)
        ) {

            config = configBuilder.getWithUnfilledThumbAsDefaultShape(unfilledThumbColor, config)
            updatePaintColors()
            redraw()
        }
    }

    /**
     * Sets the unfilled thumb to be a user specified drawable.
     */
    fun setUnfilledThumbToCustomDrawable(unfilledThumbDrawable: Drawable) {
        if (config.unfilledThumb is DrawnComponent.Default ||
            (config.unfilledThumb is DrawnComponent.UserProvided &&
                    (config.unfilledThumb as DrawnComponent.UserProvided).drawable != unfilledThumbDrawable)
        ) {

            config = configBuilder.getWithUnfilledThumbAsDrawable(unfilledThumbDrawable, config)
            redraw()
        }
    }

    /**
     * Sets the size of the thumb. This will actually set both its width and height to be this value.
     * Both the default shape and a user specified drawable will share this thumbSize.
     */
    fun setThumbSize(thumbSize: Int) {
        if (config.thumbSize != thumbSize) {
            config = configBuilder.getWithThumbSize(thumbSize, config)
            redraw()
        }
    }

    /**
     * Sets the size of the filled track on the cross axis of the StageStepBar orientation. E.g
     * if the orientation is vertical then this will set the track width and vice versa.
     */
    fun setCrossAxisFilledTrackSize(crossAxisTrackSize: Int) {
        if (config.crossAxisSizeFilledTrack != crossAxisTrackSize) {
            config = configBuilder.getWithCrossAxisFilledTrackSize(crossAxisTrackSize, config)
            redraw()
        }
    }

    /**
     * Sets the size of the unfilled track on the cross axis of the StageStepBar orientation. E.g
     * if the orientation is vertical then this will set the track width and vice versa.
     */
    fun setCrossAxisUnfilledTrackSize(crossAxisTrackSize: Int) {
        if (config.crossAxisSizeUnfilledTrack != crossAxisTrackSize) {
            config = configBuilder.getWithCrossAxisUnfilledTrackSize(crossAxisTrackSize, config)
            redraw()
        }
    }

    /**
     * Sets whether the thumbs should be drawn or not.
     */
    fun setThumbsVisible(visible: Boolean) {
        if (config.showThumbs != visible) {
            config = configBuilder.getWithShowThumbs(visible, config)
            redraw()
        }
    }

    override fun onLifecycleStart() {
        if (animatedProgress != currentProgress) {
            redraw()
        } else {
            invalidate()
        }
    }

    override fun onLifecycleStop() {
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
        if (animatedProgress != currentProgress) {
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
            duration = config.animationDuration
        }
        progressAnimator?.start()
    }

    private fun redraw() {
        currentProgress = calculateNewProgress()

        if (config.shouldAnimate) {
            animatedInvalidation()
        } else {
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

        drawUnfilledTrack(canvas)

        if (numOfStages > 1) {
            val progressBarEnd = drawFilledTrack(canvas, drawReverse)

            if (config.showThumbs) {
                drawThumbs(canvas, progressBarEnd, drawReverse)
            }
        }
    }

    private fun drawUnfilledTrack(canvas: Canvas) {
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int

        when (config.orientation) {
            Orientation.Horizontal -> {
                left = config.thumbSize / 2
                top = canvas.height / 2 - config.crossAxisSizeUnfilledTrack / 2
                right = canvas.width - config.thumbSize / 2
                bottom = canvas.height / 2 + config.crossAxisSizeUnfilledTrack / 2
            }
            Orientation.Vertical -> {
                left = canvas.width / 2 - config.crossAxisSizeUnfilledTrack / 2
                top = config.thumbSize / 2
                right = canvas.width / 2 + config.crossAxisSizeUnfilledTrack / 2
                bottom = canvas.height - config.thumbSize / 2
            }
        }

        when (config.unfilledTrack) {
            is DrawnComponent.Default -> {
                unfilledTrackRect.set(left, top, right, bottom)
                canvas.drawRect(unfilledTrackRect, unfilledTrackPaint)
            }
            is DrawnComponent.UserProvided -> {
                val drawable = (config.unfilledTrack as DrawnComponent.UserProvided).drawable
                drawable.setBounds(left, top, right, bottom)
                drawable.draw(canvas)
            }
        }
    }

    private fun drawFilledTrack(canvas: Canvas, drawReverse: Boolean): Int {
        if (config.currentState != null) {
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
                    canvas.drawRect(filledTrackRect, filledTrackPaint)
                }
                is DrawnComponent.UserProvided -> {
                    val drawable = (config.filledTrack as DrawnComponent.UserProvided).drawable
                    drawable.setBounds(left, top, right, bottom)
                    drawable.draw(canvas)
                }
            }

            return progressBarEnd
        }

        return 0
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
                config.currentState == null -> false
                drawReverse -> progressBarEnd <= centerOfThisThumb
                else -> progressBarEnd >= centerOfThisThumb
            }

            val paint = if (shouldFillThumb) filledThumbPaint else unfilledThumbPaint

            when (val drawnComponent =
                if (shouldFillThumb) config.filledThumb else config.unfilledThumb) {
                is DrawnComponent.Default -> {
                    when (config.orientation) {
                        Orientation.Horizontal -> {
                            canvas.drawCircle(
                                centerOfThisThumb.toFloat(),
                                canvas.height / 2f,
                                config.thumbSize / 2f,
                                paint
                            )
                        }
                        Orientation.Vertical -> {
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
                    when (config.orientation) {
                        Orientation.Horizontal -> {
                            drawnComponent.drawable.setBounds(
                                centerOfThisThumb - (config.thumbSize / 2f).toInt(),
                                (canvas.height / 2f).toInt() - (config.thumbSize / 2f).toInt(),
                                centerOfThisThumb + (config.thumbSize / 2f).toInt(),
                                (canvas.height / 2f).toInt() + (config.thumbSize / 2f).toInt(),
                            )
                            drawnComponent.drawable.draw(canvas)
                        }
                        Orientation.Vertical -> {
                            drawnComponent.drawable.setBounds(
                                (canvas.width / 2f).toInt() - (config.thumbSize / 2f).toInt(),
                                centerOfThisThumb - (config.thumbSize / 2f).toInt(),
                                (canvas.width / 2f).toInt() + (config.thumbSize / 2f).toInt(),
                                centerOfThisThumb + (config.thumbSize / 2f).toInt()
                            )
                            drawnComponent.drawable.draw(canvas)
                        }
                    }
                }
            }
        }
    }

    private fun getDefaultConfig(): StageStepBarConfig {
        return StageStepBarConfig(
            stageStepConfig = listOf(1),
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
            unfilledThumb = DrawnComponent.Default(
                color = ContextCompat.getColor(context, R.color.default_thumb_unfilled_color)
            ),
            thumbSize = resources.getDimensionPixelSize(R.dimen.default_thumb_size),
            crossAxisSizeFilledTrack = resources.getDimensionPixelOffset(R.dimen.default_track_cross_axis_size),
            crossAxisSizeUnfilledTrack = resources.getDimensionPixelOffset(R.dimen.default_track_cross_axis_size),
            showThumbs = true,
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
        val unfilledThumb: DrawnComponent,
        val thumbSize: Int,
        val crossAxisSizeFilledTrack: Int,
        val crossAxisSizeUnfilledTrack: Int,
        val showThumbs: Boolean,
    )

    enum class Orientation {
        Horizontal, Vertical
    }

    enum class HorizontalDirection {
        Auto, Ltr, Rtl
    }

    enum class VerticalDirection {
        Ttb, Btt
    }

    internal sealed class DrawnComponent {
        data class UserProvided(
            val drawable: Drawable,
        ) : DrawnComponent()

        data class Default(
            @ColorInt val color: Int,
        ) : DrawnComponent()
    }

    data class State(val stage: Int, val step: Int)
}