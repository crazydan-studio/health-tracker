package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.crazydan.studio.app.healthtracker.model.HealthPerson
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
fun DeletedHealthTypesScreen(
    healthPerson: HealthPerson?,
    healthTypes: List<HealthType>?,
    eventDispatch: EventDispatch,
) {
    if (healthPerson == null || healthTypes == null) {
        HealthDataLoadingScreen()
        return
    }

    DeletedHealthDataScreen(
        title = {
            Text(
                getPersonLabel("已删除健康数据类型", healthPerson)
            )
        },
        dataList = healthTypes,
        onClearAll = {
            eventDispatch(Event.ClearDeletedHealthTypesOfPerson(healthPerson.id))
        },
        onNavigateBack = {
            eventDispatch(Event.NavBack())
        },
    ) { type ->
        HealthTypeCard(
            type = type,
            actions = HealthDataCardActions(
                onUndelete = {
                    eventDispatch(Event.UndeleteHealthType(type.id))
                },
            ),
        )
    }
}