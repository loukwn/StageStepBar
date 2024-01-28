package com.loukwn.snapshot_test

import android.graphics.Color
import android.widget.FrameLayout
import android.widget.TextView
import app.cash.paparazzi.Paparazzi
import com.loukwn.stagestepbar.StageStepBar
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ViewTests {

    @get:Rule
    val paparazzi = Paparazzi()

    private lateinit var stageStepBar: StageStepBar
    private lateinit var parentView: FrameLayout
    private lateinit var testNameTextView: TextView

    private val defaultThumbSize by lazy {
        paparazzi.resources.getDimensionPixelSize(R.dimen.default_thumb_size)
    }

    private val defaultTrackSize by lazy {
        paparazzi.resources.getDimensionPixelSize(R.dimen.default_track_cross_axis_size)
    }

    @After
    fun tearDown() {
        paparazzi.close()
    }

    @Before
    fun setup() {
        parentView = paparazzi.inflate(R.layout.layout_to_test)
        stageStepBar = parentView.findViewById(R.id.stageStepbar)
        testNameTextView = parentView.findViewById(R.id.testName)
        stageStepBar.setAnimate(false)
    }

    /**
     * This sets up some defaults so that all tests have the same base. The aim is to avoid as much
     * as possible having errors leak to multiple different tests.
     */
    private fun prepareViewDefaults() {
        stageStepBar.setStageStepConfig(listOf(5, 5, 5))
        stageStepBar.setCurrentState(StageStepBar.State(1, 3))
        stageStepBar.setThumbsVisible(true)
        stageStepBar.setThumbSize(defaultThumbSize)
        stageStepBar.setCrossAxisFilledTrackSize(defaultTrackSize)
        stageStepBar.setCrossAxisUnfilledTrackSize(defaultTrackSize)
        stageStepBar.setDrawTracksBehindThumbs(true)
        stageStepBar.setOrientation(StageStepBar.Orientation.Horizontal)
        stageStepBar.setHorizontalDirection(StageStepBar.HorizontalDirection.Ltr)
        stageStepBar.setFilledTrackToNormalShape(Color.BLACK)
        stageStepBar.setUnfilledTrackToNormalShape(Color.LTGRAY)
        stageStepBar.setFilledThumbToNormalShape(Color.BLACK)
        stageStepBar.setUnfilledThumbToNormalShape(Color.GRAY)
    }

    @Test
    fun `Default configuration`() {
        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `7 stages`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setCurrentState(null)
        stageStepBar.setStageStepConfig(listOf(9, 9, 9, 9, 9, 9))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Null State`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setCurrentState(null)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Non null state`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setCurrentState(StageStepBar.State(1, 2))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Fully completed (max stage)`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setCurrentState(StageStepBar.State(3, 0))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Fully completed (max step on second to last stage)`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setCurrentState(StageStepBar.State(2, 5))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Fully completed (max stage more than max steps)`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setCurrentState(StageStepBar.State(2, 512))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Fully completed (more than max steps and stages)`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setCurrentState(StageStepBar.State(20, 512))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Lots of steps between two stages`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setCurrentState(StageStepBar.State(1, 478))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Random colors Horizontal LTR`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setFilledThumbToNormalShape(Color.parseColor("#ff00ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#ff0000"))
        stageStepBar.setFilledTrackToNormalShape(Color.parseColor("#0000ff"))
        stageStepBar.setUnfilledTrackToNormalShape(Color.parseColor("#00ff00"))
        stageStepBar.setOrientation(StageStepBar.Orientation.Horizontal)
        stageStepBar.setHorizontalDirection(StageStepBar.HorizontalDirection.Ltr)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Random colors Horizontal RTL`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setFilledThumbToNormalShape(Color.parseColor("#ff00ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#ff0000"))
        stageStepBar.setFilledTrackToNormalShape(Color.parseColor("#0000ff"))
        stageStepBar.setUnfilledTrackToNormalShape(Color.parseColor("#00ff00"))
        stageStepBar.setOrientation(StageStepBar.Orientation.Horizontal)
        stageStepBar.setHorizontalDirection(StageStepBar.HorizontalDirection.Rtl)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Random colors Vertical TTB`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setFilledThumbToNormalShape(Color.parseColor("#ff00ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#ff0000"))
        stageStepBar.setFilledTrackToNormalShape(Color.parseColor("#0000ff"))
        stageStepBar.setUnfilledTrackToNormalShape(Color.parseColor("#00ff00"))
        stageStepBar.setOrientation(StageStepBar.Orientation.Vertical)
        stageStepBar.setVerticalDirection(StageStepBar.VerticalDirection.Ttb)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Random colors Vertical BTT`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setFilledThumbToNormalShape(Color.parseColor("#ff00ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#ff0000"))
        stageStepBar.setFilledTrackToNormalShape(Color.parseColor("#0000ff"))
        stageStepBar.setUnfilledTrackToNormalShape(Color.parseColor("#00ff00"))
        stageStepBar.setOrientation(StageStepBar.Orientation.Vertical)
        stageStepBar.setVerticalDirection(StageStepBar.VerticalDirection.Btt)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Alpha on filled thumb with normal shape`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setFilledThumbToNormalShape(Color.parseColor("#55ff00ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#ff0000"))
        stageStepBar.setFilledTrackToNormalShape(Color.parseColor("#0000ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#00ff00"))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Alpha on filled thumb and filled track with normal shape`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setFilledThumbToNormalShape(Color.parseColor("#55ff00ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#ff0000"))
        stageStepBar.setFilledTrackToNormalShape(Color.parseColor("#550000ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#00ff00"))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Custom drawables random state with active thumb`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        val activeThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable_3)!!
        val filledThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable)!!
        val unfilledThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable_2)!!
        val filledTrack = paparazzi.context.getDrawable(R.drawable.gradient_drawable)!!
        val unfilledTrack = paparazzi.context.getDrawable(R.drawable.gradient_drawable_2)!!

        stageStepBar.setActiveThumbToCustomDrawable(activeThumb)
        stageStepBar.setFilledThumbToCustomDrawable(filledThumb)
        stageStepBar.setUnfilledThumbToCustomDrawable(unfilledThumb)
        stageStepBar.setFilledTrackToCustomDrawable(filledTrack)
        stageStepBar.setUnfilledTrackToCustomDrawable(unfilledTrack)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Custom drawables random state without active thumb`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        val filledThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable)!!
        val unfilledThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable_2)!!
        val filledTrack = paparazzi.context.getDrawable(R.drawable.gradient_drawable)!!
        val unfilledTrack = paparazzi.context.getDrawable(R.drawable.gradient_drawable_2)!!

        stageStepBar.clearActiveThumb()
        stageStepBar.setFilledThumbToCustomDrawable(filledThumb)
        stageStepBar.setUnfilledThumbToCustomDrawable(unfilledThumb)
        stageStepBar.setFilledTrackToCustomDrawable(filledTrack)
        stageStepBar.setUnfilledTrackToCustomDrawable(unfilledTrack)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Custom drawables with alpha random state and tracks drawn behind thumbs`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        val activeThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable_3)!!
        val filledThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable)!!
        val unfilledThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable_2)!!
        val filledTrack = paparazzi.context.getDrawable(R.drawable.gradient_drawable)!!
        val unfilledTrack = paparazzi.context.getDrawable(R.drawable.gradient_drawable_2)!!

        stageStepBar.setActiveThumbToCustomDrawable(activeThumb, .4f)
        stageStepBar.setFilledThumbToCustomDrawable(filledThumb, .5f)
        stageStepBar.setUnfilledThumbToCustomDrawable(unfilledThumb, .3f)
        stageStepBar.setFilledTrackToCustomDrawable(filledTrack, .7f)
        stageStepBar.setUnfilledTrackToCustomDrawable(unfilledTrack, .4f)
        stageStepBar.setDrawTracksBehindThumbs(true)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Custom drawables with alpha random state and tracks NOT drawn behind thumbs`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        val activeThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable_3)!!
        val filledThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable)!!
        val unfilledThumb = paparazzi.context.getDrawable(R.drawable.custom_shape_drawable_2)!!
        val filledTrack = paparazzi.context.getDrawable(R.drawable.gradient_drawable)!!
        val unfilledTrack = paparazzi.context.getDrawable(R.drawable.gradient_drawable_2)!!

        stageStepBar.setActiveThumbToCustomDrawable(activeThumb, .4f)
        stageStepBar.setFilledThumbToCustomDrawable(filledThumb, .5f)
        stageStepBar.setUnfilledThumbToCustomDrawable(unfilledThumb, .3f)
        stageStepBar.setFilledTrackToCustomDrawable(filledTrack, .7f)
        stageStepBar.setUnfilledTrackToCustomDrawable(unfilledTrack, .4f)
        stageStepBar.setDrawTracksBehindThumbs(false)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Custom sizes of components`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        val thumbSize = paparazzi.context.resources.getDimensionPixelSize(R.dimen.custom_thumb_size)
        val filledTrackSize =
            paparazzi.context.resources.getDimensionPixelSize(R.dimen.custom_filled_track_size)
        val unfilledTrackSize =
            paparazzi.context.resources.getDimensionPixelSize(R.dimen.custom_unfilled_track_size)

        stageStepBar.setCrossAxisFilledTrackSize(filledTrackSize)
        stageStepBar.setCrossAxisUnfilledTrackSize(unfilledTrackSize)
        stageStepBar.setThumbSize(thumbSize)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `0 size components`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setCrossAxisFilledTrackSize(0)
        stageStepBar.setCrossAxisUnfilledTrackSize(0)
        stageStepBar.setThumbSize(0)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `No thumbs`() {
        // General setup
        prepareViewDefaults()

        // Things that are tested
        stageStepBar.setStageStepConfig(listOf(100))
        stageStepBar.setCurrentState(StageStepBar.State(0, 42))
        stageStepBar.setThumbsVisible(false)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    private fun snap(snapName: String?) {
        testNameTextView.text = "View test:\n$snapName"
        paparazzi.snapshot(parentView, "")
    }
}
