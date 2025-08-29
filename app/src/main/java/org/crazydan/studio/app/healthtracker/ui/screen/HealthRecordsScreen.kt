package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.ui.component.AddHealthRecordDialog
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataChart

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordsScreen(
    healthPerson: StateFlow<HealthPerson?>,
    healthType: StateFlow<HealthType?>,
    healthRecords: StateFlow<List<HealthRecord>>,
    onAddRecord: (HealthRecord) -> Unit,
    onNavigateBack: () -> Unit
) {
    // 使用 collectAsState() 将 StateFlow 转换为 Compose 状态
    val currentHealthPerson by healthPerson.collectAsState()
    val currentHealthType by healthType.collectAsState()
    val recordList by healthRecords.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentHealthType?.name ?: "健康数据") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
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
        AddHealthRecordDialog(
            healthPerson = currentHealthPerson,
            healthType = currentHealthType,
            onDismiss = { showAddDialog = false },
            onConfirm = { record ->
                onAddRecord(record)
                showAddDialog = false
            }
        )
    }
}