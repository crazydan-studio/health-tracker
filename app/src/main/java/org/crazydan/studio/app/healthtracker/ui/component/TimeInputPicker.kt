package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import org.crazydan.studio.app.healthtracker.R
import org.crazydan.studio.app.healthtracker.util.Pattern_HH_mm
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
    modifier: Modifier = Modifier,
    value: LocalTime? = null,
    label: @Composable (() -> Unit)? = null,
    format: String = Pattern_HH_mm,
    onValueChange: ((LocalTime) -> Unit)? = null,
) {
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    selectedTime = value

    var showTimePicker by remember { mutableStateOf(false) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern(format) }

    ClickableReadonlyTextField(
        modifier = modifier,
        value = selectedTime?.format(timeFormatter) ?: "",
        label = label,
        trailingIcon = {
            Icon(
                Icons.Default.Schedule,
                contentDescription =
                    stringResource(R.string.btn_select_time),
            )
        },
        onClick = { showTimePicker = true }
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
            title = { Text(text = stringResource(R.string.btn_select_time)) }
        )
    }
}