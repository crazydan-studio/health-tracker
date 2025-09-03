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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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

    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf(HealthLimit()) }
    val measures = remember { mutableStateListOf<HealthMeasure>() }

    editType?.let { type ->
        name = type.name
        unit = type.unit
        limit = type.limit
        type.measures.let { measures.addAll(it) }
    }

    var showEditMeasureDialog by remember { mutableStateOf(false) }
    var editMeasure by remember { mutableStateOf<HealthMeasure?>(null) }

    AddOrEditHealthDataScreen(
        title = {
            Text(
                (if (editType == null) "添加" else "编辑") + "健康类型"
            )
        },
        onNavigateBack = { eventDispatch(Event.NavBack()) },
        canSave = {
            name.isNotBlank() && unit.isNotBlank()
        },
        onSave = {
            val type = HealthType(
                id = editType?.id ?: 0,
                personId = healthPerson.id,
                name = name.trim(),
                unit = unit.trim(),
                limit = limit,
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
                    onValueChange = { name = it },
                    label = { Text("类型名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("单位") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OutlinedTextField(
                        value = limit.lower?.toString() ?: "",
                        onValueChange = {
                            limit = limit.copy(lower = it.toFloatOrNull())
                        },
                        label = { Text("下限值 (可选)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )

                    OutlinedTextField(
                        value = limit.upper?.toString() ?: "",
                        onValueChange = {
                            limit = limit.copy(upper = it.toFloatOrNull())
                        },
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
                    Text("测量指标", style = MaterialTheme.typography.headlineSmall)
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
            onSave = {
                measures.add(it)
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
    var editMeasure by remember { mutableStateOf(measure) }

    val canSave = fun(): Boolean {
        return editMeasure.name.isNotBlank()
                && editMeasure.limit.lower != null
                && editMeasure.limit.upper != null
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(
                if (editMeasure.code.isEmpty())
                    "添加测量指标"
                else "编辑测量指标"
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (canSave()) {
                        val m =
                            if (editMeasure.code.isEmpty())
                                editMeasure.copy(code = genMeasureCode())
                            else editMeasure
                        onSave(m)

                        onClose()
                    }
                },
                enabled = canSave(),
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(
                modifier = Modifier.alpha(0.5f),
                onClick = onClose,
            ) {
                Text("取消")
            }
        },
        text = {
            OutlinedTextField(
                value = editMeasure.name,
                onValueChange = { editMeasure = editMeasure.copy(name = it) },
                label = { Text("指标名称") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = editMeasure.limit.lower?.toString() ?: "",
                    onValueChange = {
                        editMeasure = editMeasure.copy(
                            limit = editMeasure.limit.copy(lower = it.toFloatOrNull())
                        )
                    },
                    label = { Text("下限值") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )

                OutlinedTextField(
                    value = editMeasure.limit.upper?.toString() ?: "",
                    onValueChange = {
                        editMeasure = editMeasure.copy(
                            limit = editMeasure.limit.copy(upper = it.toFloatOrNull())
                        )
                    },
                    label = { Text("上限值") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    )
}