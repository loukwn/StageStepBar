package com.loukwn.stagestepbar_compose_example.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@ExperimentalUnitApi
@Composable
internal fun AnimateListItem(
    shouldAnimate: Boolean,
    animationDuration: Int,
    onAnimationStateChanged: (Boolean) -> Unit,
    onAnimationDurationChanged: (Int) -> Unit,
) {
    Column {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Animate?",
            fontSize = TextUnit(18f, TextUnitType.Sp),
            fontWeight = FontWeight.ExtraBold
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = shouldAnimate,
                onCheckedChange = {
                    onAnimationStateChanged(it)
                },
                modifier = Modifier.padding(end = 16.dp)
            )

            TextField(
                value = "$animationDuration",
                enabled = shouldAnimate,
                label = { Text("Anim. Duration") },
                onValueChange = {
                    try {
                        onAnimationDurationChanged(it.toInt())
                    } catch (_: NumberFormatException) {
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                ),
                modifier = Modifier
                    .padding(top = 8.dp, start = 8.dp),
            )
        }
    }
}

@ExperimentalUnitApi
@Preview
@Composable
private fun Preview() {
    AnimateListItem(
        shouldAnimate = true,
        animationDuration = 500,
        onAnimationStateChanged = {},
        onAnimationDurationChanged = {})
}