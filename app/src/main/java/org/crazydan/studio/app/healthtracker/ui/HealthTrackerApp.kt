package org.crazydan.studio.app.healthtracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthRecordFilter
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.HealthViewModel
import org.crazydan.studio.app.healthtracker.ui.screen.AddOrEditHealthPersonScreen
import org.crazydan.studio.app.healthtracker.ui.screen.AddOrEditHealthRecordScreen
import org.crazydan.studio.app.healthtracker.ui.screen.AddOrEditHealthTypeScreen
import org.crazydan.studio.app.healthtracker.ui.screen.DeletedHealthPersonsScreen
import org.crazydan.studio.app.healthtracker.ui.screen.DeletedHealthRecordsScreen
import org.crazydan.studio.app.healthtracker.ui.screen.DeletedHealthTypesScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthPersonsScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthRecordDetailsScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthRecordsScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthTypesScreen

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Composable
fun HealthTrackerApp() {
    val viewModel: HealthViewModel = hiltViewModel()
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    val eventDispatch = fun(e: Event) {
        dispatchEvent(e, viewModel, navController, coroutineScope)
    }

    NavHost(
        navController = navController,
        startDestination = "healthPersons",
    ) {
        composable("healthPersons") {
            ShowHealthPersonsScreen(
                viewModel = viewModel,
                eventDispatch = eventDispatch,
            )
        }
        composable("addHealthPerson") {
            AddOrEditHealthPersonScreen(
                eventDispatch = eventDispatch,
            )
        }
        composable("editHealthPerson/{personId}") { backStackEntry ->
            val personId = requireArg(backStackEntry, "personId").toLong()

            LaunchedEffectWithHealthPerson(
                personId = personId,
                viewModel = viewModel,
            ) { person ->
                AddOrEditHealthPersonScreen(
                    editPerson = person,
                    eventDispatch = eventDispatch,
                )
            }
        }
        composable("deletedHealthPersons") {
            ShowDeletedHealthPersonsScreen(
                viewModel = viewModel,
                eventDispatch = eventDispatch,
            )
        }
        //
        composable("healthTypes/{personId}") { backStackEntry ->
            val personId = requireArg(backStackEntry, "personId").toLong()

            ShowHealthTypesScreen(
                personId = personId,
                viewModel = viewModel,
                eventDispatch = eventDispatch,
            )
        }
        composable("addHealthType/{personId}") { backStackEntry ->
            val personId = requireArg(backStackEntry, "personId").toLong()

            LaunchedEffectWithHealthPerson(
                personId = personId,
                viewModel = viewModel,
            ) { person ->
                AddOrEditHealthTypeScreen(
                    healthPerson = person,
                    eventDispatch = eventDispatch,
                )
            }
        }
        composable("editHealthType/{typeId}/{personId}") { backStackEntry ->
            val typeId = requireArg(backStackEntry, "typeId").toLong()
            val personId = requireArg(backStackEntry, "personId").toLong()

            LaunchedEffectWithHealthType(
                typeId = typeId,
                personId = personId,
                viewModel = viewModel,
            ) { type, person ->
                AddOrEditHealthTypeScreen(
                    editType = type,
                    healthPerson = person,
                    eventDispatch = eventDispatch,
                )
            }
        }
        composable("deletedHealthTypes/{personId}") { backStackEntry ->
            val personId = requireArg(backStackEntry, "personId").toLong()

            ShowDeletedHealthTypesScreen(
                personId = personId,
                viewModel = viewModel,
                eventDispatch = eventDispatch,
            )
        }
        //
        composable("healthRecords/{typeId}/{personId}/{filterStartDate}/{filterEndDate}") { backStackEntry ->
            val typeId = requireArg(backStackEntry, "typeId").toLong()
            val personId = requireArg(backStackEntry, "personId").toLong()
            val filter = HealthRecordFilter(
                startDate = requireArg(backStackEntry, "filterStartDate").toLong(),
                endDate = requireArg(backStackEntry, "filterEndDate").toLong(),
            )

            ShowHealthRecordsScreen(
                typeId = typeId,
                personId = personId,
                viewModel = viewModel,
                filter = filter,
                eventDispatch = eventDispatch,
            )
        }
        composable("addHealthRecord/{typeId}/{personId}") { backStackEntry ->
            val typeId = requireArg(backStackEntry, "typeId").toLong()
            val personId = requireArg(backStackEntry, "personId").toLong()

            LaunchedEffectWithHealthRecord(
                typeId = typeId,
                personId = personId,
                viewModel = viewModel,
            ) { _, type, person, tags ->
                AddOrEditHealthRecordScreen(
                    healthPerson = person,
                    healthType = type,
                    healthRecordTags = tags,
                    eventDispatch = eventDispatch,
                )
            }
        }
        composable("editHealthRecord/{recordId}/{typeId}/{personId}") { backStackEntry ->
            val recordId = requireArg(backStackEntry, "recordId").toLong()
            val typeId = requireArg(backStackEntry, "typeId").toLong()
            val personId = requireArg(backStackEntry, "personId").toLong()

            LaunchedEffectWithHealthRecord(
                recordId = recordId,
                typeId = typeId,
                personId = personId,
                viewModel = viewModel,
            ) { record, type, person, tags ->
                AddOrEditHealthRecordScreen(
                    editRecord = record,
                    healthPerson = person,
                    healthType = type,
                    healthRecordTags = tags,
                    eventDispatch = eventDispatch,
                )
            }
        }
        //
        composable("healthRecordDetails/{typeId}/{personId}") { backStackEntry ->
            val typeId = requireArg(backStackEntry, "typeId").toLong()
            val personId = requireArg(backStackEntry, "personId").toLong()

            ShowHealthRecordDetailsScreen(
                typeId = typeId,
                personId = personId,
                viewModel = viewModel,
                eventDispatch = eventDispatch,
            )
        }
        composable("deletedHealthRecords/{typeId}/{personId}") { backStackEntry ->
            val typeId = requireArg(backStackEntry, "typeId").toLong()
            val personId = requireArg(backStackEntry, "personId").toLong()

            ShowDeletedHealthRecordsScreen(
                typeId = typeId,
                personId = personId,
                viewModel = viewModel,
                eventDispatch = eventDispatch,
            )
        }
    }
}

private fun requireArg(backStackEntry: NavBackStackEntry, arg: String): String {
    return backStackEntry.arguments!!.getString(arg)!!
}

@Composable
private fun ShowHealthPersonsScreen(
    viewModel: HealthViewModel,
    eventDispatch: EventDispatch,
) {
    var healthPersons by remember { mutableStateOf<List<HealthPerson>?>(null) }
    var deletedHealthPersonAmount by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        // Note: 在产生多个 Flow 时，需通过 combine 或 launch 执行调用，
        // 其中，combine 将一次性得到所有 Flow 的结果，而 launch 将无序得到每个结果，
        // 会造成 UI 多次渲染
        combine(
            viewModel.getHealthPersons(),
            viewModel.countDeletedHealthPersons(),
        ) { list, amount ->
            healthPersons = list
            deletedHealthPersonAmount = amount
        }
            .collectLatest { it }
    }

    HealthPersonsScreen(
        healthPersons = healthPersons,
        deletedPersonAmount = deletedHealthPersonAmount,
        eventDispatch = eventDispatch,
    )
}

@Composable
private fun ShowDeletedHealthPersonsScreen(
    viewModel: HealthViewModel,
    eventDispatch: EventDispatch,
) {
    var healthPersons by remember { mutableStateOf<List<HealthPerson>?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getDeletedHealthPersons().collectLatest { list ->
            healthPersons = list
        }
    }

    DeletedHealthPersonsScreen(
        healthPersons = healthPersons,
        eventDispatch = eventDispatch,
    )
}

@Composable
private fun ShowHealthTypesScreen(
    personId: Long,
    viewModel: HealthViewModel,
    eventDispatch: EventDispatch,
) {
    var healthPerson by remember { mutableStateOf<HealthPerson?>(null) }
    var healthTypes by remember { mutableStateOf<List<HealthType>>(emptyList()) }
    var deletedHealthTypeAmount by remember { mutableLongStateOf(0L) }

    LaunchedEffect(personId) {
        combine(
            viewModel.getHealthPerson(personId),
            viewModel.getHealthTypes(personId),
            viewModel.countDeletedHealthTypes(personId),
        ) { person, types, amount ->
            healthPerson = person
            healthTypes = types
            deletedHealthTypeAmount = amount
        }
            .collectLatest { it }
    }

    HealthTypesScreen(
        healthPerson = healthPerson,
        healthTypes = healthTypes,
        deletedTypeAmount = deletedHealthTypeAmount,
        eventDispatch = eventDispatch,
    )
}

@Composable
private fun ShowDeletedHealthTypesScreen(
    personId: Long,
    viewModel: HealthViewModel,
    eventDispatch: EventDispatch,
) {
    var healthPerson by remember { mutableStateOf<HealthPerson?>(null) }
    var healthTypes by remember { mutableStateOf<List<HealthType>?>(null) }

    LaunchedEffect(personId) {
        combine(
            viewModel.getHealthPerson(personId),
            viewModel.getDeletedHealthTypes(personId),
        ) { person, types ->
            healthPerson = person
            healthTypes = types
        }
            .collectLatest { it }
    }

    DeletedHealthTypesScreen(
        healthPerson = healthPerson,
        healthTypes = healthTypes,
        eventDispatch = eventDispatch,
    )
}

@Composable
private fun ShowHealthRecordsScreen(
    typeId: Long,
    personId: Long,
    filter: HealthRecordFilter,
    viewModel: HealthViewModel,
    eventDispatch: EventDispatch,
) {
    var healthType by remember { mutableStateOf<HealthType?>(null) }
    var healthPerson by remember { mutableStateOf<HealthPerson?>(null) }
    var healthRecords by remember { mutableStateOf<List<HealthRecord>>(emptyList()) }

    LaunchedEffect(typeId, personId) {
        combine(
            viewModel.getHealthType(typeId),
            viewModel.getHealthPerson(personId),
            viewModel.getHealthRecords(
                typeId,
                filter.startDate,
                filter.endDate,
            ),
        ) { type, person, records ->
            healthType = type
            healthPerson = person
            healthRecords = records
        }
            .collectLatest { it }
    }

    HealthRecordsScreen(
        healthType = healthType,
        healthPerson = healthPerson,
        healthRecords = healthRecords,
        healthRecordFilter = filter,
        eventDispatch = eventDispatch,
    )
}

@Composable
private fun ShowHealthRecordDetailsScreen(
    typeId: Long,
    personId: Long,
    viewModel: HealthViewModel,
    eventDispatch: EventDispatch,
) {
    var healthType by remember { mutableStateOf<HealthType?>(null) }
    var healthPerson by remember { mutableStateOf<HealthPerson?>(null) }
    var healthRecords by remember { mutableStateOf<List<HealthRecord>>(emptyList()) }
    var deletedRecordAmount by remember { mutableLongStateOf(0L) }

    LaunchedEffect(typeId, personId) {
        combine(
            viewModel.getHealthType(typeId),
            viewModel.getHealthPerson(personId),
            viewModel.getHealthRecords(typeId, 0, 0),
            viewModel.countDeletedHealthRecords(typeId),
        ) { type, person, records, amount ->
            healthType = type
            healthPerson = person
            healthRecords = records
            deletedRecordAmount = amount
        }
            .collectLatest { it }
    }

    HealthRecordDetailsScreen(
        healthPerson = healthPerson,
        healthType = healthType,
        healthRecords = healthRecords,
        deletedRecordAmount = deletedRecordAmount,
        eventDispatch = eventDispatch,
    )
}

@Composable
private fun ShowDeletedHealthRecordsScreen(
    typeId: Long,
    personId: Long,
    viewModel: HealthViewModel,
    eventDispatch: EventDispatch,
) {
    var healthType by remember { mutableStateOf<HealthType?>(null) }
    var healthPerson by remember { mutableStateOf<HealthPerson?>(null) }
    var healthRecords by remember { mutableStateOf<List<HealthRecord>?>(null) }

    LaunchedEffect(typeId, personId) {
        combine(
            viewModel.getHealthType(typeId),
            viewModel.getHealthPerson(personId),
            viewModel.getDeletedHealthRecords(typeId),
        ) { type, person, records ->
            healthType = type
            healthPerson = person
            healthRecords = records
        }
            .collectLatest { it }
    }

    DeletedHealthRecordsScreen(
        healthPerson = healthPerson,
        healthType = healthType,
        healthRecords = healthRecords,
        eventDispatch = eventDispatch,
    )
}

@Composable
private fun LaunchedEffectWithHealthPerson(
    personId: Long,
    viewModel: HealthViewModel,
    content: @Composable (HealthPerson?) -> Unit,
) {
    var healthPerson by remember { mutableStateOf<HealthPerson?>(null) }

    LaunchedEffect(personId) {
        viewModel.getHealthPerson(personId).collectLatest {
            healthPerson = it
        }
    }

    content(healthPerson)
}

@Composable
private fun LaunchedEffectWithHealthType(
    typeId: Long,
    personId: Long,
    viewModel: HealthViewModel,
    content: @Composable (HealthType?, HealthPerson?) -> Unit,
) {
    var healthType by remember { mutableStateOf<HealthType?>(null) }
    var healthPerson by remember { mutableStateOf<HealthPerson?>(null) }

    LaunchedEffect(typeId, personId) {
        combine(
            viewModel.getHealthType(typeId),
            viewModel.getHealthPerson(personId),
        ) { type, person ->
            healthType = type
            healthPerson = person
        }
            .collectLatest { it }
    }

    content(healthType, healthPerson)
}

@Composable
private fun LaunchedEffectWithHealthRecord(
    recordId: Long? = null,
    typeId: Long,
    personId: Long,
    viewModel: HealthViewModel,
    content: @Composable (HealthRecord?, HealthType?, HealthPerson?, List<String>) -> Unit,
) {
    var healthType by remember { mutableStateOf<HealthType?>(null) }
    var healthPerson by remember { mutableStateOf<HealthPerson?>(null) }
    var healthRecord by remember { mutableStateOf<HealthRecord?>(null) }
    var healthRecordTags by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(recordId, typeId, personId) {
        combine(
            recordId?.let { viewModel.getHealthRecord(recordId) } ?: flowOf(null),
            viewModel.getHealthType(typeId),
            viewModel.getHealthPerson(personId),
            viewModel.getHealthRecordTags(typeId),
        ) { record, type, person, tags ->
            healthType = type
            healthPerson = person
            healthRecord = record
            healthRecordTags = tags
        }
            .collectLatest { it }
    }

    content(healthRecord, healthType, healthPerson, healthRecordTags)
}
