package com.loukwn.snapshot_test

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

    private fun snap(snapName: String?) {
        testNameTextView.text = "View test:\n$snapName"
        paparazzi.snapshot(parentView, snapName)
    }
}