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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
import org.crazydan.studio.app.healthtracker.ui.screen.SyncHealthDataScreen
import kotlin.reflect.typeOf

private var dispatcher: (Message) -> Unit = {}
fun dispatch(msg: Message) {
    dispatcher(msg)
}

fun dispatch(block: () -> Message) {
    dispatch(block())
}

private val navTypeMap = mapOf(
    typeOf<HealthRecordFilter>() to HealthRecordFilterNavType
)

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

    dispatcher = { msg ->
        dispatchMessage(msg, viewModel, navController, coroutineScope)
    }

    NavHost(
        navController = navController,
        startDestination = Route.HealthPersons,
    ) {
        //
        composable<Route.SyncHealthData> {
            SyncHealthDataScreen()
        }

        //
        composable<Route.HealthPersons> {
            ShowHealthPersonsScreen(
                viewModel = viewModel,
            )
        }
        composable<Route.AddHealthPerson> {
            AddOrEditHealthPersonScreen()
        }
        composable<Route.EditHealthPerson> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.EditHealthPerson>()

            LaunchedEffectWithHealthPerson(
                personId = route.personId,
                viewModel = viewModel,
            ) { person ->
                AddOrEditHealthPersonScreen(
                    editPerson = person,
                )
            }
        }
        composable<Route.DeletedHealthPersons> {
            ShowDeletedHealthPersonsScreen(
                viewModel = viewModel,
            )
        }

        //
        composable<Route.HealthTypes> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.HealthTypes>()

            ShowHealthTypesScreen(
                personId = route.personId,
                viewModel = viewModel,
            )
        }
        composable<Route.AddHealthType> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.AddHealthType>()

            LaunchedEffectWithHealthPerson(
                personId = route.personId,
                viewModel = viewModel,
            ) { person ->
                AddOrEditHealthTypeScreen(
                    healthPerson = person,
                )
            }
        }
        composable<Route.EditHealthType> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.EditHealthType>()

            LaunchedEffectWithHealthType(
                typeId = route.typeId,
                personId = route.personId,
                viewModel = viewModel,
            ) { type, person ->
                AddOrEditHealthTypeScreen(
                    editType = type,
                    healthPerson = person,
                )
            }
        }
        composable<Route.DeletedHealthTypes> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.DeletedHealthTypes>()

            ShowDeletedHealthTypesScreen(
                personId = route.personId,
                viewModel = viewModel,
            )
        }

        //
        composable<Route.HealthRecords>(typeMap = navTypeMap) { backStackEntry ->
            val route = backStackEntry.toRoute<Route.HealthRecords>()
            val includeDate = backStackEntry.savedStateHandle.get<Long>("filterIncludedDate")

            ShowHealthRecordsScreen(
                typeId = route.typeId,
                personId = route.personId,
                filter = route.filter.includeDate(includeDate),
                viewModel = viewModel,
            )
        }
        composable<Route.AddHealthRecord> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.AddHealthRecord>()

            LaunchedEffectWithHealthRecord(
                typeId = route.typeId,
                personId = route.personId,
                viewModel = viewModel,
            ) { _, type, person, tags ->
                AddOrEditHealthRecordScreen(
                    healthPerson = person,
                    healthType = type,
                    healthRecordTags = tags,
                )
            }
        }
        composable<Route.EditHealthRecord> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.EditHealthRecord>()

            LaunchedEffectWithHealthRecord(
                recordId = route.recordId,
                typeId = route.typeId,
                personId = route.personId,
                viewModel = viewModel,
            ) { record, type, person, tags ->
                AddOrEditHealthRecordScreen(
                    editRecord = record,
                    healthPerson = person,
                    healthType = type,
                    healthRecordTags = tags,
                )
            }
        }
        composable<Route.HealthRecordDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.HealthRecordDetails>()

            ShowHealthRecordDetailsScreen(
                typeId = route.typeId,
                personId = route.personId,
                viewModel = viewModel,
            )
        }
        composable<Route.DeletedHealthRecords> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.DeletedHealthRecords>()

            ShowDeletedHealthRecordsScreen(
                typeId = route.typeId,
                personId = route.personId,
                viewModel = viewModel,
            )
        }
    }
}

@Composable
private fun ShowHealthPersonsScreen(
    viewModel: HealthViewModel,
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
    )
}

@Composable
private fun ShowDeletedHealthPersonsScreen(
    viewModel: HealthViewModel,
) {
    var healthPersons by remember { mutableStateOf<List<HealthPerson>?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getDeletedHealthPersons().collectLatest { list ->
            healthPersons = list
        }
    }

    DeletedHealthPersonsScreen(
        healthPersons = healthPersons,
    )
}

@Composable
private fun ShowHealthTypesScreen(
    personId: Long,
    viewModel: HealthViewModel,
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
    )
}

@Composable
private fun ShowDeletedHealthTypesScreen(
    personId: Long,
    viewModel: HealthViewModel,
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
    )
}

@Composable
private fun ShowHealthRecordsScreen(
    typeId: Long,
    personId: Long,
    filter: HealthRecordFilter,
    viewModel: HealthViewModel,
) {
    var healthType by remember { mutableStateOf<HealthType?>(null) }
    var healthPerson by remember { mutableStateOf<HealthPerson?>(null) }
    var healthRecords by remember { mutableStateOf<List<HealthRecord>>(emptyList()) }

    LaunchedEffect(typeId, personId) {
        combine(
            viewModel.getHealthType(typeId),
            viewModel.getHealthPerson(personId),
            viewModel.getHealthRecords(typeId, filter),
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
    )
}

@Composable
private fun ShowHealthRecordDetailsScreen(
    typeId: Long,
    personId: Long,
    viewModel: HealthViewModel,
) {
    var healthType by remember { mutableStateOf<HealthType?>(null) }
    var healthPerson by remember { mutableStateOf<HealthPerson?>(null) }
    var healthRecords by remember { mutableStateOf<List<HealthRecord>>(emptyList()) }
    var deletedRecordAmount by remember { mutableLongStateOf(0L) }

    LaunchedEffect(typeId, personId) {
        combine(
            viewModel.getHealthType(typeId),
            viewModel.getHealthPerson(personId),
            viewModel.getHealthRecords(typeId),
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
    )
}

@Composable
private fun ShowDeletedHealthRecordsScreen(
    typeId: Long,
    personId: Long,
    viewModel: HealthViewModel,
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
