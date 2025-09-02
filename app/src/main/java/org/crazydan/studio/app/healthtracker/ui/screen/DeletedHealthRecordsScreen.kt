package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.Event
import org.crazydan.studio.app.healthtracker.ui.EventDispatch
import org.crazydan.studio.app.healthtracker.ui.component.DeletedHealthDataScreen
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCardActions
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataLoadingScreen

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-02
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletedHealthRecordsScreen(
    healthPerson: HealthPerson?,
    healthType: HealthType?,
    healthRecords: List<HealthRecord>?,
    eventDispatch: EventDispatch,
) {
    if (healthPerson == null || healthType == null || healthRecords == null) {
        HealthDataLoadingScreen()
        return
    }

    DeletedHealthDataScreen(
        title = {
            Text(
                getPersonLabel("已删除${healthType.name}数据", healthPerson)
            )
        },
        dataList = healthRecords,
        onClearAll = {
            eventDispatch(Event.ClearDeletedHealthRecordsOfType(healthType.id))
        },
        onNavigateBack = {
            eventDispatch(Event.NavBack())
        },
    ) { record ->
        HealthRecordCard(
            type = healthType,
            record = record,
            actions = HealthDataCardActions(
                onUndelete = {
                    eventDispatch(Event.UndeleteHealthRecord(record.id))
                },
            ),
        )
    }
}