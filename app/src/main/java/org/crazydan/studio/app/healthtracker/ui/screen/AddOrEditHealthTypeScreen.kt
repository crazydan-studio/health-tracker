package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.model.HealthLimit
import org.crazydan.studio.app.healthtracker.model.HealthMeasure
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.genMeasureCode
import org.crazydan.studio.app.healthtracker.ui.Event
import org.crazydan.studio.app.healthtracker.ui.EventDispatch
import org.crazydan.studio.app.healthtracker.ui.component.AddOrEditHealthDataScreen

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditHealthTypeScreen(
    editType: HealthType? = null,
    healthPerson: HealthPerson?,
    eventDispatch: EventDispatch,
) {
    if (healthPerson == null) {
        return
    }

    val inAddMode = remember(editType) { editType == null }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(inAddMode) {
        if (inAddMode) {
            focusRequester.requestFocus()
        } else {
            focusRequester.freeFocus()
        }
    }

    var name by remember(editType) { mutableStateOf(editType?.name ?: "") }
    var unit by remember(editType) { mutableStateOf(editType?.unit ?: "") }
    var upperLimit by remember(editType) { mutableStateOf(editType?.limit?.upper?.toString() ?: "") }
    var lowerLimit by remember(editType) { mutableStateOf(editType?.limit?.lower?.toString() ?: "") }
    val measures = remember(editType) {
        editType?.measures?.toMutableStateList() ?: mutableStateListOf()
    }

    var showEditMeasureDialog by remember { mutableStateOf(false) }
    var editMeasure by remember { mutableStateOf<HealthMeasure?>(null) }

    AddOrEditHealthDataScreen(
        title = {
            Text(
                (if (inAddMode) "添加" else "编辑") + "健康类型"
            )
        },
        onNavigateBack = { eventDispatch(Event.NavBack()) },
        canSave = {
            name.isNotEmpty() && unit.isNotEmpty()
        },
        onSave = {
            val type = HealthType(
                id = editType?.id ?: 0,
                personId = healthPerson.id,
                name = name,
                unit = unit,
                limit = HealthLimit(
                    upper = upperLimit.toFloatOrNull(),
                    lower = lowerLimit.toFloatOrNull(),
                ),
                measures = measures.toList(),
            )

            if (type.id == 0L) {
                eventDispatch(Event.SaveHealthType(type))
            } else {
                eventDispatch(Event.UpdateHealthType(type))
            }
        },
    ) {
        // 基本信息
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("基本信息", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.trim() },
                    label = { Text("类型名称") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it.trim() },
                    label = { Text("单位") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OutlinedTextField(
                        value = lowerLimit,
                        onValueChange = { lowerLimit = it },
                        label = { Text("下限值 (可选)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )

                    OutlinedTextField(
                        value = upperLimit,
                        onValueChange = { upperLimit = it },
                        label = { Text("上限值 (可选)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("采集指标", style = MaterialTheme.typography.headlineSmall)
                    TextButton(onClick = {
                        editMeasure = emptyMeasure()
                        showEditMeasureDialog = true
                    }) {
                        Text("添加")
                    }
                }

                measures.forEachIndexed { index, measure ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(measure.name)
                        Text(measure.limit.toString())

                        Row {
                            TextButton(
                                onClick = { measures.removeAt(index) }
                            ) {
                                Text("删除")
                            }
                            TextButton(
                                onClick = {
                                    editMeasure = measure
                                    showEditMeasureDialog = true
                                }
                            ) {
                                Text("修改")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditMeasureDialog && editMeasure != null) {
        EditHealthMeasureDialog(
            measure = editMeasure!!,
            onSave = { m ->
                val index = measures.indexOfFirst { m.code == it.code }
                if (index < 0) {
                    measures.add(m)
                } else {
                    measures[index] = m
                }
            },
            onClose = { showEditMeasureDialog = false }
        )
    }
}

private fun emptyMeasure(): HealthMeasure {
    return HealthMeasure(code = "", name = "", limit = HealthLimit())
}

@Composable
private fun EditHealthMeasureDialog(
    measure: HealthMeasure,
    onSave: (HealthMeasure) -> Unit,
    onClose: () -> Unit,
) {
    var name by remember { mutableStateOf(measure.name) }
    var upperLimit by remember { mutableStateOf(measure.limit.upper?.toString() ?: "") }
    var lowerLimit by remember { mutableStateOf(measure.limit.lower?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(
                if (measure.code.isEmpty())
                    "添加采集指标"
                else "编辑采集指标"
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    var m = HealthMeasure(
                        code = measure.code,
                        name = name,
                        limit = HealthLimit(
                            lower = lowerLimit.toFloatOrNull(),
                            upper = upperLimit.toFloatOrNull(),
                        )
                    )
                    if (m.code.isEmpty()) {
                        m = m.copy(code = genMeasureCode())
                    }
                    onSave(m)

                    onClose()
                },
                enabled = name.isNotEmpty() && (
                        lowerLimit.toFloatOrNull() != null
                                || upperLimit.toFloatOrNull() != null
                        ),
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(onClick = onClose) {
                Text("取消")
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.trim() },
                    label = { Text("指标名称") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OutlinedTextField(
                        value = lowerLimit,
                        onValueChange = { lowerLimit = it },
                        label = { Text("下限值") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )

                    OutlinedTextField(
                        value = upperLimit,
                        onValueChange = { upperLimit = it },
                        label = { Text("上限值") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    )
}