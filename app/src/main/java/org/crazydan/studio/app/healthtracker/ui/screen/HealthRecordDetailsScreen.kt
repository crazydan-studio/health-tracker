package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.Event
import org.crazydan.studio.app.healthtracker.ui.EventDispatch
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
    eventDispatch: EventDispatch,
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
            eventDispatch(
                Event.ViewDeletedHealthRecordsOfType(
                    healthType.id,
                    healthType.personId
                )
            )
        },
        onNavigateBack = { eventDispatch(Event.NavBack()) },
    ) { record ->
        HealthRecordCard(
            type = healthType,
            record = record,
            actions = HealthDataCardActions(
                onEdit = {
                    eventDispatch(
                        Event.WillEditHealthRecord(
                            record.id,
                            record.typeId,
                            record.personId
                        )
                    )
                },
                onDelete = {
                    eventDispatch(Event.DeleteHealthRecord(record.id))
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
        val label = "${record.value}${type.unit}"
        val timestamp = formatTimestamp(record)
        val notes = record.notes.ifBlank { "无" }

        Text(text = label, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "测量时间: $timestamp")

        if (record.rangeName.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "数据范围: ${record.rangeName}")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "备注: $notes")
    }
}

private fun formatTimestamp(record: HealthRecord): String {
    return formatEpochMillis(record.timestamp, "yyyy-MM-dd HH:mm")
}