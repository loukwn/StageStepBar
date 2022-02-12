package com.loukwn.snapshot_test

import android.graphics.Color
import android.widget.FrameLayout
import android.widget.LinearLayout
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

    @Before
    fun setup() {
        parentView = paparazzi.inflate(R.layout.layout_to_test)
        stageStepBar = parentView.findViewById(R.id.stageStepbar)
        testNameTextView = parentView.findViewById(R.id.testName)
        stageStepBar.setAnimate(false)
    }

    @Test
    fun `Default configuration`() {
        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `7 stages`() {
        stageStepBar.setStageStepConfig(listOf(9, 9, 9, 9, 9, 9))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Null State`() {
        stageStepBar.setCurrentState(null)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Non null state`() {
        stageStepBar.setStageStepConfig(listOf(5, 5, 5))
        stageStepBar.setCurrentState(StageStepBar.State(1, 2))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Fully completed (max stage)`() {
        stageStepBar.setStageStepConfig(listOf(5, 5, 5))
        stageStepBar.setCurrentState(StageStepBar.State(3, 0))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Fully completed (max step on second to last stage)`() {
        stageStepBar.setStageStepConfig(listOf(5, 5, 5))
        stageStepBar.setCurrentState(StageStepBar.State(2, 5))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }


    @Test
    fun `Fully completed (max stage more than max steps)`() {
        stageStepBar.setStageStepConfig(listOf(5, 5, 5))
        stageStepBar.setCurrentState(StageStepBar.State(2, 512))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Fully completed (more than max steps and stages)`() {
        stageStepBar.setStageStepConfig(listOf(5, 5, 5))
        stageStepBar.setCurrentState(StageStepBar.State(20, 512))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Lots of steps between two stages`() {
        stageStepBar.setStageStepConfig(listOf(5, 5, 1000))
        stageStepBar.setCurrentState(StageStepBar.State(1, 478))

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Random colors Horizontal LTR`() {
        stageStepBar.setStageStepConfig(listOf(5,5,5))
        stageStepBar.setCurrentState(StageStepBar.State(1, 3))
        stageStepBar.setThumbsVisible(true)
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
        stageStepBar.setStageStepConfig(listOf(5,5,5))
        stageStepBar.setCurrentState(StageStepBar.State(1, 3))
        stageStepBar.setThumbsVisible(true)
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
        stageStepBar.setStageStepConfig(listOf(5,5,5))
        stageStepBar.setCurrentState(StageStepBar.State(1, 3))
        stageStepBar.setThumbsVisible(true)
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
        stageStepBar.setStageStepConfig(listOf(5,5,5))
        stageStepBar.setCurrentState(StageStepBar.State(1, 3))
        stageStepBar.setThumbsVisible(true)
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
        stageStepBar.setStageStepConfig(listOf(5,5,5))
        stageStepBar.setCurrentState(StageStepBar.State(1, 3))
        stageStepBar.setThumbsVisible(true)
        stageStepBar.setFilledThumbToNormalShape(Color.parseColor("#55ff00ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#ff0000"))
        stageStepBar.setFilledTrackToNormalShape(Color.parseColor("#0000ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#00ff00"))
        stageStepBar.setOrientation(StageStepBar.Orientation.Horizontal)
        stageStepBar.setHorizontalDirection(StageStepBar.HorizontalDirection.Ltr)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `Alpha on filled thumb and filled track with normal shape`() {
        stageStepBar.setStageStepConfig(listOf(5,5,5))
        stageStepBar.setCurrentState(StageStepBar.State(1, 3))
        stageStepBar.setThumbsVisible(true)
        stageStepBar.setFilledThumbToNormalShape(Color.parseColor("#55ff00ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#ff0000"))
        stageStepBar.setFilledTrackToNormalShape(Color.parseColor("#550000ff"))
        stageStepBar.setUnfilledThumbToNormalShape(Color.parseColor("#00ff00"))
        stageStepBar.setOrientation(StageStepBar.Orientation.Horizontal)
        stageStepBar.setHorizontalDirection(StageStepBar.HorizontalDirection.Ltr)

        snap(snapName = object {}.javaClass.enclosingMethod?.name)
    }

    @Test
    fun `No thumbs`() {
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