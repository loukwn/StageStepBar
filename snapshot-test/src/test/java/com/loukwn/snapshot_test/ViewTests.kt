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
    fun `Default state`() {
        val name = object{}.javaClass.enclosingMethod?.name
        snap(snapName = name)
    }

    @Test
    fun `6 stages test`() {
        stageStepBar.setStageStepConfig(listOf(9,9,9,9,9,9))

        val name = object{}.javaClass.enclosingMethod?.name
        snap(snapName = name)
    }

    @Test
    fun `State test`() {
        stageStepBar.setCurrentState(null)

        val name = object{}.javaClass.enclosingMethod?.name
        snap(snapName = name)
    }

    @Test
    fun `Fully completed`() {
        stageStepBar.setCurrentState(StageStepBar.State(3, 0))

        val name = object{}.javaClass.enclosingMethod?.name
        snap(snapName = name)
    }

    private fun snap(snapName: String?) {
        testNameTextView.text = "View test:\n$snapName"
        paparazzi.snapshot(parentView, snapName)
    }
}