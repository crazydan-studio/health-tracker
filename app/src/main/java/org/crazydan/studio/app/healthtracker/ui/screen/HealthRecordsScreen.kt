package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthRecordFilter
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.Event
import org.crazydan.studio.app.healthtracker.ui.EventDispatch
import org.crazydan.studio.app.healthtracker.ui.component.DateInputPicker
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataLoadingScreen
import org.crazydan.studio.app.healthtracker.ui.component.HealthRecordAverageCircle
import org.crazydan.studio.app.healthtracker.ui.component.HealthRecordsChart
import org.crazydan.studio.app.healthtracker.util.epochMillisToLocalDate
import org.crazydan.studio.app.healthtracker.util.toEpochMillis

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordsScreen(
    healthType: HealthType?,
    healthPerson: HealthPerson?,
    healthRecords: List<HealthRecord>,
    healthRecordFilter: HealthRecordFilter,
    eventDispatch: EventDispatch,
) {
    if (healthPerson == null || healthType == null) {
        HealthDataLoadingScreen()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        getPersonLabel(healthType.name, healthPerson)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        eventDispatch(Event.NavBack())
                    }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (healthRecords.isNotEmpty()) {
                        IconButton(onClick = {
                            eventDispatch(
                                Event.ViewHealthRecordDetailsOfType(
                                    healthType.id,
                                    healthType.personId
                                )
                            )
                        }) {
                            Icon(Icons.Default.TableView, contentDescription = "查看详情")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                eventDispatch(
                    Event.WillAddHealthRecordOfType(
                        healthType.id,
                        healthType.personId
                    )
                )
            }) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                DateInputPicker(
                    modifier = Modifier
                        .weight(1f)
                        .focusable(false),
                    value = epochMillisToLocalDate(healthRecordFilter.startDate),
                    label = { Text("开始日期") },
                    onValueChange = {
                        eventDispatch(
                            Event.ViewHealthRecordsOfType(
                                healthType.id,
                                healthType.personId,
                                filter = HealthRecordFilter(
                                    startDate = toEpochMillis(it),
                                    endDate = healthRecordFilter.endDate,
                                ),
                            )
                        )
                    },
                )

                Spacer(modifier = Modifier.width(16.dp))
                DateInputPicker(
                    modifier = Modifier
                        .weight(1f)
                        .focusable(false),
                    value = epochMillisToLocalDate(healthRecordFilter.endDate),
                    label = { Text("结束日期") },
                    onValueChange = {
                        eventDispatch(
                            Event.ViewHealthRecordsOfType(
                                healthType.id,
                                healthType.personId,
                                filter = HealthRecordFilter(
                                    startDate = healthRecordFilter.startDate,
                                    endDate = toEpochMillis(it, untilToDayEnd = true),
                                ),
                            )
                        )
                    },
                )
            }

            if (healthRecords.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("暂无数据，请点击右下角按钮添加")
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                ) {
                    if (healthType.measures.isNotEmpty()) {
                        healthType.measures.forEach { measure ->
                            HealthRecordAverageCircle(
                                label = "<${measure.name}>均值",
                                measure = measure,
                                records = healthRecords,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        HealthRecordAverageCircle(
                            label = "<${healthType.name}>均值",
                            records = healthRecords,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }

                HealthRecordsChart(
                    healthType = healthType,
                    healthRecords = healthRecords,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                )
            }
        }
    }
}