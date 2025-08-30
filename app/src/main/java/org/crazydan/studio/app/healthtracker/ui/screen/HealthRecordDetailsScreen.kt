package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCard
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataListScreen
import org.crazydan.studio.app.healthtracker.util.formatEpochMillis

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-29
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordDetailsScreen(
    healthPerson: StateFlow<HealthPerson?>,
    healthType: StateFlow<HealthType?>,
    healthRecords: StateFlow<List<HealthRecord>>,
    onDeleteRecord: (HealthRecord) -> Unit,
    onUndeleteRecord: (HealthRecord) -> Unit,
    onEditRecord: (HealthRecord) -> Unit,
    onNavigateBack: () -> Unit,
) {
    // 使用 collectAsState() 将 StateFlow 转换为 Compose 状态
    val currentHealthPerson by healthPerson.collectAsState()
    val currentHealthType by healthType.collectAsState()
    val currentHealthRecords by healthRecords.collectAsState()

    HealthDataListScreen(
        title = {
            Text(getPersonLabel(currentHealthType?.name + "记录", currentHealthPerson))
        },
        deletedMessage = { record ->
            "已删除记录【${record.value}${currentHealthType?.unit} @${formatTimestamp(record)}】"
        },
        onUndelete = onUndeleteRecord,
        onNavigateBack = onNavigateBack,
    ) { padding, afterDeleted ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(currentHealthRecords) { record ->
                HealthRecordItem(
                    type = currentHealthType!!,
                    record = record,
                    onDeleteRecord = {
                        onDeleteRecord(record)
                        afterDeleted(record)
                    },
                    onEditRecord = { onEditRecord(record) },
                )
            }
        }
    }
}

@Composable
private fun HealthRecordItem(
    type: HealthType,
    record: HealthRecord,
    onDeleteRecord: () -> Unit,
    onEditRecord: () -> Unit,
) {
    HealthDataCard(
        onEdit = onEditRecord,
        onDelete = onDeleteRecord,
    ) {
        val label = "${record.value}${type.unit}"
        val timestamp = formatTimestamp(record)

        Text(text = label, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "测量时间: $timestamp")

        if (record.rangeName.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "数据范围: ${record.rangeName}")
        }
    }
}

private fun formatTimestamp(record: HealthRecord): String {
    return formatEpochMillis(record.timestamp, "yyyy-MM-dd HH:mm")
}