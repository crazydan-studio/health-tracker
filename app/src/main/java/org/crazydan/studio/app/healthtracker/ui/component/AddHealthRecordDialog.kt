package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.util.toEpochMilli
import java.time.LocalDate
import java.time.LocalTime

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthRecordDialog(
    healthPerson: HealthPerson?,
    healthType: HealthType?,
    onDismiss: () -> Unit,
    onConfirm: (HealthRecord) -> Unit
) {
    var value by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val ranges = healthType?.ranges?.map { it.name } ?: emptyList()
    var rangeName by remember { mutableStateOf("") }
    var rangeExpanded by remember { mutableStateOf(false) }

    var timestampDate: LocalDate by remember { mutableStateOf(LocalDate.now()) }
    var timestampTime: LocalTime by remember { mutableStateOf(LocalTime.now().noSeconds()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加${healthType?.name}记录") },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("测量值 (${healthType?.unit})") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                DateInputPicker(
                    value = timestampDate,
                    label = { Text("测量日期") },
                    onValueChange = { timestampDate = it },
                )
                Spacer(modifier = Modifier.height(16.dp))
                TimeInputPicker(
                    value = timestampTime,
                    label = { Text("测量时间") },
                    onValueChange = { timestampTime = it },
                )

                Spacer(modifier = Modifier.height(16.dp))
                // 范围选择
                ExposedDropdownMenuBox(
                    expanded = rangeExpanded,
                    onExpandedChange = { rangeExpanded = !rangeExpanded }
                ) {
                    OutlinedTextField(
                        value = rangeName,
                        onValueChange = { rangeName = it },
                        label = { Text("数据范围") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = rangeExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = rangeExpanded,
                        onDismissRequest = { rangeExpanded = false }
                    ) {
                        ranges.forEach { range ->
                            DropdownMenuItem(
                                text = { Text(range) },
                                onClick = {
                                    rangeName = range
                                    rangeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("备注 (可选)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    value.toFloatOrNull()?.let {
                        if (healthType != null && healthPerson != null) {
                            onConfirm(
                                HealthRecord(
                                    value = it,
                                    timestamp = toEpochMilli(timestampDate, timestampTime),
                                    typeId = healthType.id,
                                    personId = healthPerson.id,
                                    rangeName = rangeName,
                                    notes = notes,
                                    createdAt = System.currentTimeMillis(),
                                )
                            )
                        }
                    }
                },
                enabled = value.toFloatOrNull() != null && rangeName.isNotBlank()
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}