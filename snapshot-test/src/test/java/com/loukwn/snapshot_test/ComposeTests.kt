package com.loukwn.snapshot_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.Paparazzi
import com.loukwn.stagestepbar_compose.StageStepBar
import com.loukwn.stagestepbar_compose.data.DrawnComponent
import com.loukwn.stagestepbar_compose.data.HorizontalDirection
import com.loukwn.stagestepbar_compose.data.Orientation
import com.loukwn.stagestepbar_compose.data.StageStepBarConfig
import com.loukwn.stagestepbar_compose.data.State
import com.loukwn.stagestepbar_compose.data.VerticalDirection
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ComposeTests(private val config: StageStepBarConfig, private val testName: String) {
    @get:Rule
    val paparazzi = Paparazzi()

    companion object {
        @JvmStatic
        @Parameters
        fun data(): Iterable<Array<Any>> {
            val defaultConfig = StageStepBarConfig.default()
            val baseTestConfig = getBaseTestConfig(defaultConfig)
            return listOf(
                arrayOf(defaultConfig, "Default configuration"),
                arrayOf(
                    baseTestConfig.copy(
                        currentState = null,
                        stageStepConfig = listOf(9, 9, 9, 9, 9, 9)
                    ),
                    "7 stages",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        currentState = null,
                    ),
                    "Null state",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        currentState = State(1, 2),
                    ),
                    "Non null state",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        currentState = State(3, 0),
                    ),
                    "Fully completed (max stage)",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        currentState = State(2, 5),
                    ),
                   "Fully completed (max step on second to last stage)",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        currentState = State(2, 512),
                    ),
                    "Fully completed (max stage more than max steps)",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        currentState = State(20, 512),
                    ),
                    "Fully completed (more than max steps and stages)",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        currentState = State(1, 478),
                    ),
                    "Lots of steps between two stages",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        filledThumb = DrawnComponent.Default(Color(0xffff00ff)),
                        unfilledThumb = DrawnComponent.Default(Color(0xffff0000)),
                        filledTrack = DrawnComponent.Default(Color(0xff0000ff)),
                        unfilledTrack = DrawnComponent.Default(Color(0xff00ff00)),
                        orientation = Orientation.Horizontal,
                        horizontalDirection = HorizontalDirection.Ltr,
                    ),
                    "Random colors Horizontal LTR",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        filledThumb = DrawnComponent.Default(Color(0xffff00ff)),
                        unfilledThumb = DrawnComponent.Default(Color(0xffff0000)),
                        filledTrack = DrawnComponent.Default(Color(0xff0000ff)),
                        unfilledTrack = DrawnComponent.Default(Color(0xff00ff00)),
                        orientation = Orientation.Horizontal,
                        horizontalDirection = HorizontalDirection.Rtl,
                    ),
                    "Random colors Horizontal RTL",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        filledThumb = DrawnComponent.Default(Color(0xffff00ff)),
                        unfilledThumb = DrawnComponent.Default(Color(0xffff0000)),
                        filledTrack = DrawnComponent.Default(Color(0xff0000ff)),
                        unfilledTrack = DrawnComponent.Default(Color(0xff00ff00)),
                        orientation = Orientation.Vertical,
                        verticalDirection = VerticalDirection.Ttb,
                    ),
                    "Random colors Vertical TTB",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        filledThumb = DrawnComponent.Default(Color(0xffff00ff)),
                        unfilledThumb = DrawnComponent.Default(Color(0xffff0000)),
                        filledTrack = DrawnComponent.Default(Color(0xff0000ff)),
                        unfilledTrack = DrawnComponent.Default(Color(0xff00ff00)),
                        orientation = Orientation.Vertical,
                        verticalDirection = VerticalDirection.Btt,
                    ),
                    "Random colors Vertical BTT",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        filledThumb = DrawnComponent.Default(Color(0x55ff00ff)),
                        unfilledThumb = DrawnComponent.Default(Color(0xffff0000)),
                        filledTrack = DrawnComponent.Default(Color(0xff0000ff)),
                        unfilledTrack = DrawnComponent.Default(Color(0xff00ff00)),
                    ),
                    "Alpha on filled thumb with normal shape"
                ),
                arrayOf(
                    baseTestConfig.copy(
                        filledThumb = DrawnComponent.Default(Color(0x55ff00ff)),
                        unfilledThumb = DrawnComponent.Default(Color(0xffff0000)),
                        filledTrack = DrawnComponent.Default(Color(0x550000ff)),
                        unfilledTrack = DrawnComponent.Default(Color(0xff00ff00)),
                    ),
                    "Alpha on filled thumb and filled track with normal shape"
                ),
                arrayOf(
                    baseTestConfig.copy(
                        crossAxisSizeFilledTrack = 8.dp,
                        crossAxisSizeUnfilledTrack = 10.dp,
                        thumbSize = 23.dp,
                    ),
                    "Custom sizes of components",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        crossAxisSizeFilledTrack = 0.dp,
                        crossAxisSizeUnfilledTrack = 0.dp,
                        thumbSize = 0.dp,
                    ),
                    "0 size components",
                ),
                arrayOf(
                    baseTestConfig.copy(
                        stageStepConfig = listOf(100),
                        currentState = State(0, 42),
                        showThumbs = false
                    ),
                    "No thumbs",
                )
            )
        }
    }

    @Test
    fun parameterizedTests() {
        paparazzi.snapshot(testName) {
            StageStepBarTestComposable(config, testName)
        }
    }
}

@Suppress("TestFunctionName")
@Composable
private fun StageStepBarTestComposable(config: StageStepBarConfig, testName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        StageStepBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp)
                .height(200.dp)
                .align(Alignment.TopCenter),
            config = config
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black)
                .padding(bottom = 10.dp, start = 20.dp, end = 20.dp),
            text = "Compose test:\n$testName",
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

private fun getBaseTestConfig(defaultConfig: StageStepBarConfig): StageStepBarConfig {
    return defaultConfig.copy(
        stageStepConfig = listOf(5, 5, 5),
        currentState = State(1, 3),
        showThumbs = true,
        thumbSize = 20.dp,
        crossAxisSizeFilledTrack = 6.dp,
        crossAxisSizeUnfilledTrack = 6.dp,
        drawTracksBehindThumbs = true,
        orientation = Orientation.Horizontal,
        horizontalDirection = HorizontalDirection.Ltr,
        filledTrack = DrawnComponent.Default(Color.Black),
        unfilledTrack = DrawnComponent.Default(Color.LightGray),
        filledThumb = DrawnComponent.Default(Color.Black),
        unfilledThumb = DrawnComponent.Default(Color.Gray),
    )
}