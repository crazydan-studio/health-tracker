package org.crazydan.studio.app.healthtracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.crazydan.studio.app.healthtracker.ui.screen.AddOrEditHealthPersonScreen
import org.crazydan.studio.app.healthtracker.ui.screen.AddOrEditHealthRecordScreen
import org.crazydan.studio.app.healthtracker.ui.screen.AddOrEditHealthTypeScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthPersonsScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthRecordDetailsScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthRecordsScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthTypesScreen
import org.crazydan.studio.app.healthtracker.ui.viewmodel.HealthViewModel

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Composable
fun HealthTrackerApp() {
    val navController = rememberNavController()
    val viewModel: HealthViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "healthPersons",
    ) {
        composable("healthPersons") {
            HealthPersonsScreen(
                healthPersons = viewModel.healthPersons,
                onAddPerson = {
                    navController.navigate("addHealthPerson")
                },
                onDeletePerson = { person ->
                    coroutineScope.launch {
                        viewModel.deleteHealthPerson(person.id)
                    }
                },
                onUndeletePerson = { person ->
                    coroutineScope.launch {
                        viewModel.undeleteHealthPerson(person.id)
                    }
                },
                onEditPerson = { person ->
                    coroutineScope.launch {
                        viewModel.selectHealthPerson(person)
                        navController.navigate("editHealthPerson")
                    }
                },
                onViewTypes = { person ->
                    coroutineScope.launch {
                        viewModel.selectHealthPerson(person)
                        navController.navigate("healthTypes")
                    }
                }
            )
        }
        composable("addHealthPerson") {
            AddOrEditHealthPersonScreen(
                onSave = { person ->
                    coroutineScope.launch {
                        viewModel.addHealthPerson(person)
                        navController.popBackStack()
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("editHealthPerson") {
            AddOrEditHealthPersonScreen(
                editPerson = viewModel.selectedHealthPerson,
                onSave = { person ->
                    coroutineScope.launch {
                        viewModel.updateHealthPerson(person)
                        navController.popBackStack()
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        //
        composable("healthTypes") { backStackEntry ->
            HealthTypesScreen(
                healthPerson = viewModel.selectedHealthPerson,
                healthTypes = viewModel.healthTypes,
                onAddType = {
                    navController.navigate("addHealthType")
                },
                onDeleteType = { type ->
                    coroutineScope.launch {
                        viewModel.deleteHealthType(type.id)
                    }
                },
                onUndeleteType = { type ->
                    coroutineScope.launch {
                        viewModel.undeleteHealthType(type.id)
                    }
                },
                onEditType = { type ->
                    coroutineScope.launch {
                        viewModel.selectHealthType(type)
                        navController.navigate("editHealthType")
                    }
                },
                onViewRecords = { type ->
                    coroutineScope.launch {
                        viewModel.selectHealthType(type)
                        navController.navigate("healthRecords")
                    }
                },
                onNavigateBack = {
                    navController.navigate("healthPersons")
                }
            )
        }
        composable("addHealthType") { backStackEntry ->
            AddOrEditHealthTypeScreen(
                healthPerson = viewModel.selectedHealthPerson,
                onSave = { type ->
                    coroutineScope.launch {
                        viewModel.addHealthType(type)
                        navController.popBackStack()
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("editHealthType") {
            AddOrEditHealthTypeScreen(
                editType = viewModel.selectedHealthType,
                healthPerson = viewModel.selectedHealthPerson,
                onSave = { type ->
                    coroutineScope.launch {
                        viewModel.updateHealthType(type)
                        navController.popBackStack()
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        //
        composable("healthRecords") { backStackEntry ->
            HealthRecordsScreen(
                healthPerson = viewModel.selectedHealthPerson,
                healthType = viewModel.selectedHealthType,
                healthRecords = viewModel.healthRecords,
                onAddRecord = {
                    navController.navigate("addHealthRecord")
                },
                onGotoRecordDetails = {
                    navController.navigate("healthRecordDetails")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("addHealthRecord") { backStackEntry ->
            AddOrEditHealthRecordScreen(
                healthPerson = viewModel.selectedHealthPerson,
                healthType = viewModel.selectedHealthType,
                onSave = { record ->
                    coroutineScope.launch {
                        viewModel.addHealthRecord(record)
                        navController.popBackStack()
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("editHealthRecord") {
            AddOrEditHealthRecordScreen(
                editRecord = viewModel.selectedHealthRecord,
                healthPerson = viewModel.selectedHealthPerson,
                healthType = viewModel.selectedHealthType,
                onSave = { record ->
                    coroutineScope.launch {
                        viewModel.updateHealthRecord(record)
                        navController.popBackStack()
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        //
        composable("healthRecordDetails") { backStackEntry ->
            HealthRecordDetailsScreen(
                healthPerson = viewModel.selectedHealthPerson,
                healthType = viewModel.selectedHealthType,
                healthRecords = viewModel.healthRecords,
                onDeleteRecord = { record ->
                    coroutineScope.launch {
                        viewModel.deleteHealthRecord(record.id)
                    }
                },
                onUndeleteRecord = { record ->
                    coroutineScope.launch {
                        viewModel.undeleteHealthRecord(record.id)
                    }
                },
                onEditRecord = { record ->
                    coroutineScope.launch {
                        viewModel.selectHealthRecord(record)
                        navController.navigate("editHealthRecord")
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}