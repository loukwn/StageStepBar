package com.loukwn.stagestepbar_compose_example.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.loukwn.stagestepbar_compose_example.ComponentType
import com.loukwn.stagestepbar_compose_example.presentation.common.DropDown
import com.loukwn.stagestepbar_compose.data.DrawnComponent

@ExperimentalUnitApi
@Composable
internal fun ComponentDrawableSelectionListItem(
    listOfColors: List<Color>,
    componentType: ComponentType,
    drawnComponent: DrawnComponent,
    modifier: Modifier,
    onComponentDrawableChanged: (Int, Color) -> Unit,
) {
    var colorPosition by rememberSaveable {
        val index = if (drawnComponent is DrawnComponent.Default) {
            listOfColors.indexOf(drawnComponent.color)
        } else {
            0
        }
        mutableStateOf(index)
    }

    Column {
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            text = componentType.title,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            fontWeight = FontWeight.ExtraBold
        )

        DropDown(
            modifier = modifier,
            label = "Drawable",
            initialPosition = if (drawnComponent is DrawnComponent.Default) 0 else 1,
            options = listOf("Default Shape", "User Provided 1", "User Provided 2")
        ) {
            onComponentDrawableChanged(it, listOfColors[colorPosition])
        }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(top = 8.dp)
        ) {
            Text(text = "Color of default", modifier = Modifier.padding(end = 16.dp))
            Box(
                modifier = Modifier
                    .size(80.dp, 40.dp)
                    .background(listOfColors[colorPosition])
                    .clickable {
                        val newColorPosition = (colorPosition + 1) % listOfColors.size
                        colorPosition = newColorPosition
                        onComponentDrawableChanged(0, listOfColors[newColorPosition])
                    })
        }
    }
}

@ExperimentalUnitApi
@Preview
@Composable
private fun Preview() {
    ComponentDrawableSelectionListItem(
        listOfColors = listOf(Color.Gray, Color.Magenta),
        componentType = ComponentType.FilledTrack,
        drawnComponent = DrawnComponent.Default(Color.Magenta),
        modifier = Modifier.width(300.dp),
        onComponentDrawableChanged = { _, _ -> }
    )
}