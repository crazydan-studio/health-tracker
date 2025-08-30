package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.NormalRange
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCard

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTypesScreen(
    healthPerson: StateFlow<HealthPerson?>,
    healthTypes: StateFlow<List<HealthType>>,
    onAddType: () -> Unit,
    onDeleteType: (HealthType) -> Unit,
    onEditType: (HealthType) -> Unit,
    onViewRecords: (HealthType) -> Unit,
    onNavigateBack: () -> Unit
) {
    // 使用 collectAsState() 将 StateFlow 转换为 Compose 状态
    val person by healthPerson.collectAsState()
    val types by healthTypes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        getPersonLabel("健康数据", person)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddType) {
                Icon(Icons.Default.Add, contentDescription = "添加类型")
            }
        }
    ) { padding ->
        if (types.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("暂无健康数据类型，请点击右下角按钮添加")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(types) { type ->
                    HealthTypeItem(
                        type = type,
                        onDeleteType = { },
                        onEditType = { onEditType(type) },
                        onViewRecords = { onViewRecords(type) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthTypeItem(
    type: HealthType,
    onDeleteType: () -> Unit,
    onEditType: () -> Unit,
    onViewRecords: () -> Unit,
) {
    HealthDataCard(
        onEdit = onEditType,
        onDelete = onDeleteType,
        onView = onViewRecords,
    ) {
        Text(text = type.name, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "单位: ${type.unit}")

        if (type.ranges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "正常范围:", style = MaterialTheme.typography.labelMedium)

            type.ranges.forEach { range ->
                Text(text = "  ${range.name}: ${range.lowerLimit} - ${range.upperLimit} ${type.unit}")
            }
        }
    }
}

@Preview
@Composable
private fun HealthTypeItemPreview() {
    HealthTypeItem(
        type = HealthType(
            id = 0,
            personId = 0,
            name = "血糖",
            unit = "mmol/L",
            ranges = listOf(
                NormalRange(
                    name = "餐后 2h",
                    lowerLimit = 3.2f,
                    upperLimit = 10f,
                ),
            ),
        ),
        onDeleteType = {},
        onEditType = {},
        onViewRecords = {},
    )
}