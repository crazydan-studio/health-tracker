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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataListScreen

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
        return
    }

    HealthDataListScreen(
        deletedAmount = deletedTypeAmount,
        title = {
            Text(
                getPersonLabel("健康数据", healthPerson)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                eventDispatch(
                    Event.WillAddHealthTypeOfPerson(healthPerson.id)
                )
            }) {
                Icon(Icons.Default.Add, contentDescription = "添加类型")
            }
        },
        onViewDeleted = {
            eventDispatch(
                Event.ViewDeletedHealthTypesOfPerson(healthPerson.id)
            )
        },
        onNavigateBack = {
            eventDispatch(
                Event.NavBack()
            )
        },
    ) { padding ->
        if (healthTypes.isEmpty()) {
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
                items(healthTypes) { type ->
                    HealthTypeItem(
                        type = type,
                        eventDispatch = eventDispatch,
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthTypeItem(
    type: HealthType,
    eventDispatch: EventDispatch,
) {
    HealthDataCard(
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
    ) {
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
        eventDispatch = {},
    )
}