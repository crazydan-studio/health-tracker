package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TableView
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.component.HealthRecordAverageCircle
import org.crazydan.studio.app.healthtracker.ui.component.HealthRecordsChart

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
    onAddRecord: () -> Unit,
    onGotoRecordDetails: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    // 使用 collectAsState() 将 StateFlow 转换为 Compose 状态
    val currentHealthPerson by healthPerson.collectAsState()
    val currentHealthType by healthType.collectAsState()
    val currentHealthRecords by healthRecords.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        getPersonLabel(currentHealthType?.name, currentHealthPerson)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (currentHealthRecords.isNotEmpty()) {
                        IconButton(onClick = onGotoRecordDetails) {
                            Icon(Icons.Default.TableView, contentDescription = "查看详情")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddRecord) {
                Icon(Icons.Default.Add, contentDescription = "添加记录")
            }
        }
    ) { padding ->
        if (currentHealthRecords.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("暂无${currentHealthType?.name}数据，请点击右下角按钮添加")
            }
        } else {
            Column(
                modifier = Modifier.padding(padding)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                ) {
                    currentHealthType!!.ranges.forEach { range ->
                        HealthRecordAverageCircle(
                            label = "<${range.name}>均值",
                            range = range,
                            records = currentHealthRecords,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    HealthRecordsChart(
                        healthType = currentHealthType!!,
                        healthRecords = currentHealthRecords,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}