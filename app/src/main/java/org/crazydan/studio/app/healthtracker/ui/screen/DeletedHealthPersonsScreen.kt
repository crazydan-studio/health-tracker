package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.crazydan.studio.app.healthtracker.model.HealthPerson
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
fun DeletedHealthPersonsScreen(
    healthPersons: List<HealthPerson>?,
    eventDispatch: EventDispatch,
) {
    if (healthPersons == null) {
        HealthDataLoadingScreen()
        return
    }

    DeletedHealthDataScreen(
        title = {
            Text("已删除人员信息")
        },
        dataList = healthPersons,
        onClearAll = {
            eventDispatch(Event.ClearDeletedHealthPersons())
        },
        onNavigateBack = {
            eventDispatch(Event.NavBack())
        },
    ) { person ->
        HealthPersonCard(
            person = person,
            actions = HealthDataCardActions(
                onUndelete = {
                    eventDispatch(Event.UndeleteHealthPerson(person.id))
                },
            ),
        )
    }
}