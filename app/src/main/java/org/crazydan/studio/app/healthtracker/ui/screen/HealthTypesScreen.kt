package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.NormalRange
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.Event
import org.crazydan.studio.app.healthtracker.ui.EventDispatch
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCard
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataCardActions
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataListScreen
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataLoadingScreen

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTypesScreen(
    healthPerson: HealthPerson?,
    healthTypes: List<HealthType>,
    deletedTypeAmount: Long,
    eventDispatch: EventDispatch,
) {
    if (healthPerson == null) {
        HealthDataLoadingScreen()
        return
    }

    HealthDataListScreen(
        deletedAmount = deletedTypeAmount,
        dataList = healthTypes,
        title = {
            Text(
                getPersonLabel("健康数据", healthPerson)
            )
        },
        onAddData = {
            eventDispatch(
                Event.WillAddHealthTypeOfPerson(healthPerson.id)
            )
        },
        onViewDeleted = {
            eventDispatch(
                Event.ViewDeletedHealthTypesOfPerson(healthPerson.id)
            )
        },
        onNavigateBack = { eventDispatch(Event.NavBack()) },
    ) { type ->
        HealthTypeCard(
            type = type,
            actions = HealthDataCardActions(
                onEdit = {
                    eventDispatch(
                        Event.WillEditHealthType(
                            type.id,
                            type.personId
                        )
                    )
                },
                onDelete = {
                    eventDispatch(Event.DeleteHealthType(type.id))
                },
                onView = {
                    eventDispatch(
                        Event.ViewHealthRecordsOfType(
                            type.id,
                            type.personId
                        )
                    )
                },
            ),
        )
    }
}

@Composable
fun HealthTypeCard(
    type: HealthType,
    actions: HealthDataCardActions,
) {
    HealthDataCard(actions) {
        Text(text = type.name, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "单位: ${type.unit}")

        if (type.ranges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "正常范围:", style = MaterialTheme.typography.labelMedium)

            type.ranges.forEach { range ->
                Text(text = "  ${range.name}: ${range.lowerLimit} ~ ${range.upperLimit} ${type.unit}")
            }
        }
    }
}

@Preview
@Composable
private fun HealthTypeItemPreview() {
    HealthTypeCard(
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
        actions = HealthDataCardActions(),
    )
}