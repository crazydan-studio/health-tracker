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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.R
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthRecordFilter
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.getPersonLabel
import org.crazydan.studio.app.healthtracker.ui.Message
import org.crazydan.studio.app.healthtracker.ui.component.DateInputPicker
import org.crazydan.studio.app.healthtracker.ui.component.HealthDataLoadingScreen
import org.crazydan.studio.app.healthtracker.ui.component.HealthRecordAverageCircle
import org.crazydan.studio.app.healthtracker.ui.component.HealthRecordsChart
import org.crazydan.studio.app.healthtracker.ui.dispatch
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
                        stringResource(
                            R.string.title_health_records,
                            getPersonLabel(healthPerson),
                            healthType.name,
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        dispatch(Message.NavBack())
                    }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription =
                                stringResource(R.string.btn_back),
                        )
                    }
                },
                actions = {
                    if (healthRecords.isNotEmpty()) {
                        IconButton(onClick = {
                            dispatch(
                                Message.ViewHealthRecordDetailsOfType(
                                    healthType.id,
                                    healthType.personId
                                )
                            )
                        }) {
                            Icon(
                                Icons.Default.TableView,
                                contentDescription =
                                    stringResource(R.string.btn_details),
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                dispatch(
                    Message.WillAddHealthRecordOfType(
                        typeId = healthType.id,
                        personId = healthType.personId,
                    )
                )
            }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription =
                        stringResource(R.string.btn_add),
                )
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
                    label = { Text(stringResource(R.string.label_health_record_filter_start_date)) },
                    onValueChange = {
                        dispatch(
                            Message.ViewHealthRecordsOfType(
                                typeId = healthType.id,
                                personId = healthType.personId,
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
                    label = { Text(stringResource(R.string.label_health_record_filter_end_date)) },
                    onValueChange = {
                        dispatch(
                            Message.ViewHealthRecordsOfType(
                                typeId = healthType.id,
                                personId = healthType.personId,
                                filter = HealthRecordFilter(
                                    startDate = healthRecordFilter.startDate,
                                    endDate = toEpochMillis(it, untilToDayEnd = true),
                                ),
                            )
                        )
                    },
                )
            }

            // TODO 按标签过滤：多选并做 and 运算

            if (healthRecords.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.msg_no_data_to_add))
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
                                label = stringResource(
                                    R.string.label_health_record_stats_average,
                                    measure.name
                                ),
                                measure = measure,
                                records = healthRecords,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        HealthRecordAverageCircle(
                            label = stringResource(
                                R.string.label_health_record_stats_average,
                                healthType.name
                            ),
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