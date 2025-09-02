package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.NormalRange
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
    val ranges = remember { mutableStateListOf<NormalRange>() }

    editType?.let { type ->
        name = type.name
        unit = type.unit
        type.ranges.let { ranges.addAll(it) }
    }

    var rangeName by remember { mutableStateOf("") }
    var lowerLimit by remember { mutableStateOf("") }
    var upperLimit by remember { mutableStateOf("") }

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
                ranges = ranges.toList()
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
            }
        }

        // 添加范围
        Spacer(modifier = Modifier.height(16.dp))
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("添加正常范围", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = rangeName,
                    onValueChange = { rangeName = it },
                    label = { Text("范围名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = lowerLimit,
                        onValueChange = { lowerLimit = it },
                        label = { Text("下限值") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = upperLimit,
                        onValueChange = { upperLimit = it },
                        label = { Text("上限值") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (rangeName.isNotBlank() &&
                            lowerLimit.toFloatOrNull() != null &&
                            upperLimit.toFloatOrNull() != null
                        ) {
                            ranges.add(
                                NormalRange(
                                    name = rangeName.trim(),
                                    lowerLimit = lowerLimit.toFloat(),
                                    upperLimit = upperLimit.toFloat(),
                                )
                            )

                            // 清空输入
                            rangeName = ""
                            lowerLimit = ""
                            upperLimit = ""
                        }
                    },
                    enabled = rangeName.isNotBlank() &&
                            lowerLimit.toFloatOrNull() != null &&
                            upperLimit.toFloatOrNull() != null,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("添加范围")
                }
            }
        }

        // 已添加的范围
        if (ranges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("已添加的范围", style = MaterialTheme.typography.headlineSmall)

                    ranges.forEachIndexed { index, range ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(range.name)
                            Text("${range.lowerLimit} - ${range.upperLimit}")
                            TextButton(
                                onClick = { ranges.removeAt(index) }
                            ) {
                                Text("删除")
                            }
                        }
                    }
                }
            }
        }
    }
}