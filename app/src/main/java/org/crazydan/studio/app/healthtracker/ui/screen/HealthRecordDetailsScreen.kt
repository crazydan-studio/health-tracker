package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getMeasureNameByCode
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.Message
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCard
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCardActions
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataListScreen
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataLoadingScreen
import org.crazydan.studio.app.healthtracker.util.formatEpochMillis

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-29
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordDetailsScreen(
    healthType: HealthType?,
    healthPerson: HealthPerson?,
    healthRecords: List<HealthRecord>,
    deletedRecordAmount: Long,
) {
    if (healthPerson == null || healthType == null) {
        HealthDataLoadingScreen()
        return
    }

    HealthDataListScreen(
        deletedAmount = deletedRecordAmount,
        dataList = healthRecords,
        title = {
            Text(getPersonLabel(healthType.name + "记录", healthPerson))
        },
        onViewDeleted = {
            Message.ViewDeletedHealthRecordsOfType(
                healthType.id,
                healthType.personId
            )
        },
        onNavigateBack = { Message.NavBack() },
    ) { record ->
        HealthRecordCard(
            type = healthType,
            record = record,
            actions = HealthDataCardActions(
                onEdit = {
                    Message.WillEditHealthRecord(
                        record.id,
                        record.typeId,
                        record.personId
                    )
                },
                onDelete = {
                    Message.DeleteHealthRecord(record.id)
                },
            ),
        )
    }
}

@Composable
fun HealthRecordCard(
    type: HealthType,
    record: HealthRecord,
    actions: HealthDataCardActions,
) {
    HealthDataCard(actions) {
        val label = "${record.value} ${type.unit}"
        val timestamp = formatTimestamp(record)

        Text(text = label, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "采集时间: $timestamp")

        if (record.measure.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "采集指标: ${getMeasureNameByCode(type, record.measure)}")
        }

        if (record.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(record.tags) { data ->
                    AssistChip(
                        onClick = { },
                        label = { Text(data) },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = MaterialTheme.colorScheme.primary
                        ),
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(record: HealthRecord): String {
    return formatEpochMillis(record.timestamp, "yyyy-MM-dd HH:mm")
}

@Preview
@Composable
private fun HealthRecordCardPreview() {
    HealthRecordCard(
        type = PreviewSample().createHealthType(),
        record = PreviewSample().createHealthRecord(),
        actions = HealthDataCardActions(
            onEdit = { Message.NavBack() },
            onDelete = { Message.NavBack() },
        ),
    )
}