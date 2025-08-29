package org.crazydan.studio.app.healthtracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.crazydan.studio.app.healthtracker.ui.screen.AddHealthPersonScreen
import org.crazydan.studio.app.healthtracker.ui.screen.AddHealthRecordScreen
import org.crazydan.studio.app.healthtracker.ui.screen.AddHealthTypeScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthPersonsScreen
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
                onSelectPerson = { person ->
                    coroutineScope.launch {
                        viewModel.selectHealthPerson(person)
                        navController.navigate("healthTypes")
                    }
                }
            )
        }
        composable("addHealthPerson") {
            AddHealthPersonScreen(
                onSave = { person ->
                    coroutineScope.launch {
                        viewModel.addHealthPerson(person)
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
                onSelectType = { type ->
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
            AddHealthTypeScreen(
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
        //
        composable("healthRecords") { backStackEntry ->
            HealthRecordsScreen(
                healthPerson = viewModel.selectedHealthPerson,
                healthType = viewModel.selectedHealthType,
                healthRecords = viewModel.healthRecords,
                onAddRecord = {
                    navController.navigate("addHealthRecord")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("addHealthRecord") { backStackEntry ->
            AddHealthRecordScreen(
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
    }
}