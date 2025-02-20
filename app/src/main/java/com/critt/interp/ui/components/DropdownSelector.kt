package com.critt.interp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.critt.domain.LanguageData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    //TODO: State Hoisting -> Move to ui_common module
    options: List<LanguageData>,
    selectedOption: LanguageData?,
    onOptionSelected: (LanguageData) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedOption?.name) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedText ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        selectedText = option.name
                        expanded = false
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}