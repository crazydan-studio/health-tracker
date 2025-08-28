// HealthTypesScreen.kt
package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import org.crazydan.studio.app.healthtracker.model.HealthType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTypesScreen(
    healthTypes: StateFlow<List<HealthType>>,
    onAddType: () -> Unit,
    onSelectType: (HealthType) -> Unit
) {
    // 使用 collectAsState() 将 StateFlow 转换为 Compose 状态
    val types by healthTypes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("健康数据管理") })
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
                        onClick = { onSelectType(type) }
                    )
                }
            }
        }
    }
}

@Composable
fun HealthTypeItem(type: HealthType, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = type.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "单位: ${type.unit}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "正常范围:", style = MaterialTheme.typography.labelMedium)
            type.ranges.forEach { range ->
                Text(text = "  ${range.name}: ${range.lowerLimit} - ${range.upperLimit} ${type.unit}")
            }
        }
    }
}