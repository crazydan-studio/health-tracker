package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import kotlinx.coroutines.flow.StateFlow
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.ui.component.DateInputPicker
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
    editRecord: StateFlow<HealthRecord?>? = null,
    healthPerson: StateFlow<HealthPerson?>,
    healthType: StateFlow<HealthType?>,
    onSave: (HealthRecord) -> Unit,
    onCancel: () -> Unit,
) {
    val currentEditRecord = editRecord?.collectAsState()?.value
    val currentHealthPerson by healthPerson.collectAsState()
    val currentHealthType by healthType.collectAsState()

    var value by remember { mutableStateOf(currentEditRecord?.value?.toString() ?: "") }
    var notes by remember { mutableStateOf(currentEditRecord?.notes ?: "") }

    val ranges = currentHealthType?.ranges?.map { it.name } ?: emptyList()
    var rangeName by remember { mutableStateOf(currentEditRecord?.rangeName ?: "") }
    var rangeExpanded by remember { mutableStateOf(false) }

    val timestamp =
        if (currentEditRecord == null) null
        else epochMillisToLocalDateTime(currentEditRecord.timestamp)
    var timestampDate: LocalDate by remember {
        mutableStateOf(timestamp?.toLocalDate() ?: LocalDate.now())
    }
    var timestampTime: LocalTime by remember {
        mutableStateOf(timestamp?.toLocalTime() ?: LocalTime.now().noSeconds())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        (if (currentEditRecord == null) "添加" else "编辑") + "${currentHealthType?.name}记录"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            Button(
                onClick = {
                    value.toFloatOrNull()?.let {
                        if (currentHealthType != null && currentHealthPerson != null) {
                            onSave(
                                HealthRecord(
                                    id = currentEditRecord?.id ?: 0,
                                    value = it,
                                    timestamp = toEpochMillis(timestampDate, timestampTime),
                                    typeId = currentHealthType!!.id,
                                    personId = currentHealthPerson!!.id,
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
                Icon(Icons.Default.Save, contentDescription = "保存")
                Spacer(modifier = Modifier.padding(4.dp))
                Text("保存")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("测量值 (${currentHealthType?.unit})") },
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

                if (!ranges.isEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // 范围选择
                    ExposedDropdownMenuBox(
                        expanded = rangeExpanded,
                        onExpandedChange = { rangeExpanded = !rangeExpanded }
                    ) {
                        OutlinedTextField(
                            value = rangeName,
                            readOnly = true,
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
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("备注 (可选)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}