package org.crazydan.studio.app.healthtracker.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.Event
import org.crazydan.studio.app.healthtracker.ui.EventDispatch
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataLoadingScreen
import org.crazydan.studio.app.healthtracker.ui.component.HealthRecordAverageCircle
import org.crazydan.studio.app.healthtracker.ui.component.HealthRecordsChart

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
        if (healthRecords.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("暂无数据，请点击右下角按钮添加")
            }
        } else {
            Column(
                modifier = Modifier.padding(padding)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                ) {
                    if (healthType.ranges.isNotEmpty()) {
                        healthType.ranges.forEach { range ->
                            HealthRecordAverageCircle(
                                label = "<${range.name}>均值",
                                range = range,
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    HealthRecordsChart(
                        healthType = healthType,
                        healthRecords = healthRecords,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}