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
import org.crazydan.studio.app.healthtracker.model.HealthType

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordDialog(
    healthType: HealthType?,
    persons: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (Float, Long, String, String, String) -> Unit
) {
    var value by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var person by remember { mutableStateOf("") }
    var rangeName by remember { mutableStateOf("") }
    var personExpanded by remember { mutableStateOf(false) }
    var rangeExpanded by remember { mutableStateOf(false) }

    val ranges = healthType?.ranges?.map { it.name } ?: emptyList()
    val currentTime = System.currentTimeMillis()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加${healthType?.name}记录") },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("值 (${healthType?.unit})") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 人员选择
                ExposedDropdownMenuBox(
                    expanded = personExpanded,
                    onExpandedChange = { personExpanded = !personExpanded }
                ) {
                    OutlinedTextField(
                        value = person,
                        onValueChange = { person = it },
                        label = { Text("人员/目的") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = personExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = personExpanded,
                        onDismissRequest = { personExpanded = false }
                    ) {
                        persons.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    person = p
                                    personExpanded = false
                                }
                            )
                        }
                    }
                }

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
                        onConfirm(it, currentTime, notes, person, rangeName)
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