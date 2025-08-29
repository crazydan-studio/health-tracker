package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * https://github.com/marosseleng/compose-material3-datetime-pickers
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-29
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TimeInputPicker(
    value: LocalTime? = null,
    label: @Composable (() -> Unit)? = null,
    format: String = "HH:mm",
    onValueChange: ((LocalTime) -> Unit)? = null,
) {
    var selectedTime: LocalTime? by remember {
        mutableStateOf(value)
    }

    var showTimePicker by remember { mutableStateOf(false) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern(format) }

    OutlinedTextField(
        value = selectedTime?.format(timeFormatter) ?: "",
        onValueChange = { /* 禁止直接编辑 */ },
        label = label,
        trailingIcon = {
            IconButton(onClick = { showTimePicker = true }) {
                Icon(Icons.Default.Schedule, contentDescription = "选择时间")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true
    )

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = selectedTime ?: LocalTime.now().noSeconds(),
            onDismissRequest = { showTimePicker = false },
            onTimeChange = {
                selectedTime = it
                showTimePicker = false
                onValueChange?.invoke(it)
            },
            title = { Text(text = "选择时间") }
        )
    }
}