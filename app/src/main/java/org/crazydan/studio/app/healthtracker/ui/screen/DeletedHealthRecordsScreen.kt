package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.crazydan.studio.app.healthtracker.R
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.Message
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
) {
    if (healthPerson == null || healthType == null || healthRecords == null) {
        HealthDataLoadingScreen()
        return
    }

    DeletedHealthDataScreen(
        title = {
            Text(
                stringResource(
                    R.string.title_deleted_health_records,
                    getPersonLabel(healthPerson),
                    healthType.name,
                )
            )
        },
        dataList = healthRecords,
        onClearAll = {
            Message.ClearDeletedHealthRecordsOfType(healthType.id)
        },
        onNavigateBack = { Message.NavBack },
    ) { record ->
        HealthRecordCard(
            type = healthType,
            record = record,
            actions = HealthDataCardActions(
                onUndelete = {
                    Message.UndeleteHealthRecord(record.id)
                },
            ),
        )
    }
}