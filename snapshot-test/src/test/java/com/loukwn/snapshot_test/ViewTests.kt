package com.loukwn.snapshot_test

import android.widget.FrameLayout
import android.widget.LinearLayout
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

    @Before
    fun setup() {
        parentView = paparazzi.inflate(R.layout.layout_to_test)
        stageStepBar = parentView.getChildAt(0) as StageStepBar
    }

    @After
    fun cleanup() {
        paparazzi.close()
    }

    @Test
    fun Default() {
        paparazzi.snapshot(parentView, "default")
    }

    @Test
    fun `Many stages test -`() {
        stageStepBar.setStageStepConfig(listOf(9,9,9,9,9,9))
        paparazzi.snapshot(parentView, "6 stages")
    }

    @Test
    fun `State test -`() {
        stageStepBar.setCurrentState(null)
        paparazzi.snapshot(parentView, "null")
    }
}