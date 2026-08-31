package com.homiq.app.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import java.time.LocalDate
import com.homiq.app.R
import com.homiq.app.ui.util.formatEpochDay

@Composable
fun DateField(
    label: String,
    epochDay: Long,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    val currentDate = LocalDate.ofEpochDay(epochDay)

    val showPicker = {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected(
                    LocalDate.of(
                        year,
                        month + 1,
                        dayOfMonth,
                    ).toEpochDay(),
                )
            },
            currentDate.year,
            currentDate.monthValue - 1,
            currentDate.dayOfMonth,
        ).show()
    }

    Box(
        modifier = modifier.clickable(onClick = showPicker),
    ) {
        OutlinedTextField(
            value = formatEpochDay(epochDay, locale),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = stringResource(
                        R.string.choose_date,
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun <T> SelectionField(
    label: String,
    selectedText: String,
    options: List<T>,
    optionText: @Composable (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = enabled,
                    onClick = { expanded = true },
                ),
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text(label) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                val text = optionText(option)
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    },
                )
            }
        }
    }
}
