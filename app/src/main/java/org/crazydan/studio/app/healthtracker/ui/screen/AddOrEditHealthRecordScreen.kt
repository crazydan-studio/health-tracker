package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getMeasureNameByCode
import org.crazydan.studio.app.healthtracker.ui.Event
import org.crazydan.studio.app.healthtracker.ui.EventDispatch
import org.crazydan.studio.app.healthtracker.ui.component.AddOrEditHealthDataScreen
import org.crazydan.studio.app.healthtracker.ui.component.DateInputPicker
import org.crazydan.studio.app.healthtracker.ui.component.TagsEditor
import org.crazydan.studio.app.healthtracker.ui.component.TimeInputPicker
import org.crazydan.studio.app.healthtracker.util.epochMillisToLocalDateTime
import org.crazydan.studio.app.healthtracker.util.toEpochMillis
import java.time.LocalDate
import java.time.LocalTime

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditHealthRecordScreen(
    editRecord: HealthRecord? = null,
    healthType: HealthType?,
    healthPerson: HealthPerson?,
    healthRecordTags: List<String>,
    eventDispatch: EventDispatch,
) {
    if (healthPerson == null || healthType == null) {
        return
    }

    var rangeExpanded by remember { mutableStateOf(false) }

    var measureCode by remember(editRecord) {
        mutableStateOf(
            editRecord?.measure
                ?: healthType.measures.firstOrNull()?.code
                ?: ""
        )
    }
    var value by remember(editRecord) {
        mutableStateOf(editRecord?.value?.toString() ?: "")
    }
    val tags = remember(editRecord) {
        editRecord?.tags?.toMutableStateList() ?: mutableStateListOf()
    }

    val timestamp = epochMillisToLocalDateTime(editRecord?.timestamp)
    var timestampDate by remember(editRecord) {
        mutableStateOf(timestamp?.toLocalDate() ?: LocalDate.now())
    }
    var timestampTime by remember(editRecord) {
        mutableStateOf(timestamp?.toLocalTime() ?: LocalTime.now().noSeconds())
    }

    AddOrEditHealthDataScreen(
        title = {
            Text(
                (if (editRecord == null) "添加" else "编辑") + "${healthType.name}记录"
            )
        },
        onNavigateBack = { eventDispatch(Event.NavBack()) },
        canSave = {
            value.toFloatOrNull() != null
        },
        onSave = {
            val record = HealthRecord(
                id = editRecord?.id ?: 0,
                value = value.toFloatOrNull()!!,
                timestamp = toEpochMillis(timestampDate, timestampTime),
                typeId = healthType.id,
                personId = healthPerson.id,
                measure = measureCode,
                tags = tags.toList(),
                createdAt = System.currentTimeMillis(),
            )

            if (record.id == 0L) {
                eventDispatch(Event.SaveHealthRecord(record))
            } else {
                eventDispatch(Event.UpdateHealthRecord(record))
            }
        },
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("采集值 (${healthType.unit})") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        DateInputPicker(
            value = timestampDate,
            label = { Text("采集日期") },
            onValueChange = { timestampDate = it },
        )
        Spacer(modifier = Modifier.height(16.dp))
        TimeInputPicker(
            value = timestampTime,
            label = { Text("采集时间") },
            onValueChange = { timestampTime = it },
        )

        if (!healthType.measures.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            // 范围选择
            ExposedDropdownMenuBox(
                expanded = rangeExpanded,
                onExpandedChange = { rangeExpanded = !rangeExpanded }
            ) {
                OutlinedTextField(
                    value = getMeasureNameByCode(healthType, measureCode),
                    readOnly = true,
                    onValueChange = { },
                    label = { Text("采集指标") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = rangeExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )

                ExposedDropdownMenu(
                    expanded = rangeExpanded,
                    onDismissRequest = { rangeExpanded = false }
                ) {
                    healthType.measures.forEach { measure ->
                        DropdownMenuItem(
                            text = { Text(measure.name) },
                            onClick = {
                                measureCode = measure.code
                                rangeExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TagsEditor(
            allTags = healthRecordTags,
            selectedTags = tags,
            onAdd = {
                if (!tags.contains(it)) {
                    tags.add(it)
                }
            },
            onRemove = { tags.remove(it) },
        )
    }
}