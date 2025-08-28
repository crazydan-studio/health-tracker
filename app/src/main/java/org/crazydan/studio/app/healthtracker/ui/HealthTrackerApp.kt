// HealthTrackerApp.kt
package org.crazydan.studio.app.healthtracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.ui.screen.AddHealthTypeScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthDataScreen
import org.crazydan.studio.app.healthtracker.ui.screen.HealthTypesScreen
import org.crazydan.studio.app.healthtracker.ui.viewmodel.HealthViewModel

@Composable
fun HealthTrackerApp() {
    val navController = rememberNavController()
    val viewModel: HealthViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = "healthTypes") {
        composable("healthTypes") {
            HealthTypesScreen(
                healthTypes = viewModel.healthTypes,
                onAddType = { navController.navigate("addType") },
                onSelectType = { type ->
                    coroutineScope.launch {
                        viewModel.selectHealthType(type)
                        navController.navigate("healthData/${type.id}")
                    }
                }
            )
        }
        composable("addType") {
            AddHealthTypeScreen(
                onSave = { healthType ->
                    coroutineScope.launch {
                        viewModel.addHealthType(healthType)
                        navController.popBackStack()
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("healthData/{typeId}") { backStackEntry ->
            val typeId = backStackEntry.arguments?.getString("typeId")?.toLongOrNull()
            HealthDataScreen(
                healthType = viewModel.selectedHealthType,
                records = viewModel.healthRecords,
                persons = viewModel.persons,
                onAddRecord = { value, timestamp, notes, person, rangeName ->
                    coroutineScope.launch {
                        typeId?.let {
                            viewModel.addHealthRecord(
                                HealthRecord(
                                    typeId = it,
                                    value = value,
                                    timestamp = timestamp,
                                    notes = notes,
                                    person = person,
                                    rangeName = rangeName
                                )
                            )
                        }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}