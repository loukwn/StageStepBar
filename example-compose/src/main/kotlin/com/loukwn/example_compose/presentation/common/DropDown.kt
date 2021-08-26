package com.loukwn.example_compose.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.loukwn.example_compose.ui.theme.dropDownLabelColor
import com.loukwn.example_compose.ui.theme.dropdownBg


@ExperimentalUnitApi
@Composable
internal fun DropDown(
    modifier: Modifier,
    label: String,
    options: List<String>,
    initialPosition: Int,
    onSelectionMade: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        var expanded by remember { mutableStateOf(false) }
        var selectedPosition by rememberSaveable { mutableStateOf(initialPosition) }

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { expanded = true }
                    .background(
                        MaterialTheme.colors.dropdownBg,
                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        label,
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        color = MaterialTheme.colors.dropDownLabelColor
                    )
                    Text(options[selectedPosition])
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }
            Divider(color = MaterialTheme.colors.onSurface, thickness = .5.dp)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier,
        ) {
            repeat(options.size) {
                DropdownMenuItem(onClick = {
                    onSelectionMade(it)
                    selectedPosition = it
                    expanded = false
                }) {
                    Text(options[it])
                }
            }
        }
    }
}

@ExperimentalUnitApi
@Preview
@Composable
private fun PreviewDropDown() {
    DropDown(
        modifier = Modifier
            .width(200.dp),
        label = "Choose an option",
        initialPosition = 0,
        options = listOf("Option 1", "Option 2"),
        onSelectionMade = {}
    )
}