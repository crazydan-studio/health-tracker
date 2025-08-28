package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
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
import kotlinx.coroutines.flow.StateFlow
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.ui.component.AddRecordDialog
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataChart

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordsScreen(
    healthType: StateFlow<HealthType?>,
    healthRecords: StateFlow<List<HealthRecord>>,
    persons: StateFlow<List<String>>,
    onAddRecord: (Float, Long, String, String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    // 使用 collectAsState() 将 StateFlow 转换为 Compose 状态
    val currentHealthType by healthType.collectAsState()
    val recordList by healthRecords.collectAsState()
    val personList by persons.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentHealthType?.name ?: "健康数据") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加记录")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // 显示数据图表
            currentHealthType?.let { type ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    HealthDataChart(
                        healthType = type,
                        healthRecords = recordList,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // 添加记录对话框
    if (showAddDialog) {
        AddRecordDialog(
            healthType = currentHealthType,
            persons = personList,
            onDismiss = { showAddDialog = false },
            onConfirm = { value, timestamp, notes, person, rangeName ->
                onAddRecord(value, timestamp, notes, person, rangeName)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonSelector(
    persons: List<String>,
    selectedPerson: String,
    onSelectPerson: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedPerson,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("全部") },
                onClick = {
                    onSelectPerson("")
                    expanded = false
                }
            )
            persons.forEach { person ->
                DropdownMenuItem(
                    text = { Text(person) },
                    onClick = {
                        onSelectPerson(person)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeSelector(
    ranges: List<String>,
    selectedRange: String,
    onSelectRange: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedRange,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("全部") },
                onClick = {
                    onSelectRange("")
                    expanded = false
                }
            )
            ranges.forEach { range ->
                DropdownMenuItem(
                    text = { Text(range) },
                    onClick = {
                        onSelectRange(range)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun HealthRecordItem(record: HealthRecord, unit: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "值: ${record.value} $unit")
                Text(text = java.util.Date(record.timestamp).toString())
            }
            if (record.rangeName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "范围: ${record.rangeName}")
            }
            if (record.person.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "人员: ${record.person}")
            }
            if (record.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "备注: ${record.notes}")
            }
        }
    }
}