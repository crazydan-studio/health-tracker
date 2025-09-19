package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import org.crazydan.studio.app.healthtracker.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * https://github.com/marosseleng/compose-material3-datetime-pickers
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-29
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DateInputPicker(
    modifier: Modifier = Modifier,
    value: LocalDate? = null,
    label: @Composable (() -> Unit)? = null,
    format: String = "yyyy-MM-dd",
    onValueChange: ((LocalDate) -> Unit)? = null,
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    selectedDate = value

    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern(format) }

    ClickableReadonlyTextField(
        modifier = modifier,
        value = selectedDate?.format(dateFormatter) ?: "",
        label = label,
        trailingIcon = {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription =
                    stringResource(R.string.btn_select_date),
            )
        },
        onClick = { showDatePicker = true }
    )

    if (showDatePicker) {
        DatePickerDialog(
            initialDate = selectedDate,
            onDismissRequest = { showDatePicker = false },
            onDateChange = {
                selectedDate = it
                showDatePicker = false
                onValueChange?.invoke(it)
            },
            title = { Text(text = stringResource(R.string.btn_select_date)) }
        )
    }
}