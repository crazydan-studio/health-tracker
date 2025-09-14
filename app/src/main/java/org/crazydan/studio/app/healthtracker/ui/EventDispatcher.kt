package org.crazydan.studio.app.healthtracker.ui

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.crazydan.studio.app.healthtracker.model.HealthViewModel

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-02
 */
fun dispatchEvent(
    e: Event,
    viewModel: HealthViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope,
) {
    val goback = fun() {
        navController.popBackStack()
    }

    when (e) {
        is Event.NavBack -> {
            goback()
        }

        is Event.WillAddHealthPerson -> {
            navController.navigate("addHealthPerson")
        }

        is Event.WillEditHealthPerson -> {
            navController.navigate("editHealthPerson/${e.id}")
        }

        is Event.ViewDeletedHealthPersons -> {
            navController.navigate("deletedHealthPersons")
        }

        is Event.SaveHealthPerson -> {
            coroutineScope.launch {
                viewModel.addHealthPerson(e.data)
                goback()
            }
        }

        is Event.UpdateHealthPerson -> {
            coroutineScope.launch {
                viewModel.updateHealthPerson(e.data)
                goback()
            }
        }

        is Event.DeleteHealthPerson -> {
            coroutineScope.launch {
                viewModel.deleteHealthPerson(e.id)
            }
        }

        is Event.UndeleteHealthPerson -> {
            coroutineScope.launch {
                viewModel.undeleteHealthPerson(e.id)
            }
        }

        is Event.ClearDeletedHealthPersons -> {
            coroutineScope.launch {
                viewModel.clearDeletedHealthPersons()
            }
        }

        //
        is Event.WillAddHealthTypeOfPerson -> {
            navController.navigate("addHealthType/${e.id}")
        }

        is Event.ViewHealthTypesOfPerson -> {
            navController.navigate("healthTypes/${e.id}")
        }

        is Event.ViewDeletedHealthTypesOfPerson -> {
            navController.navigate("deletedHealthTypes/${e.id}")
        }

        //
        is Event.WillEditHealthType -> {
            navController.navigate("editHealthType/${e.id}/${e.personId}")
        }


        is Event.SaveHealthType -> {
            coroutineScope.launch {
                viewModel.addHealthType(e.data)
                goback()
            }
        }

        is Event.UpdateHealthType -> {
            coroutineScope.launch {
                viewModel.updateHealthType(e.data)
                goback()
            }
        }

        is Event.DeleteHealthType -> {
            coroutineScope.launch {
                viewModel.deleteHealthType(e.id)
            }
        }

        is Event.UndeleteHealthType -> {
            coroutineScope.launch {
                viewModel.undeleteHealthType(e.id)
            }
        }

        is Event.ClearDeletedHealthTypesOfPerson -> {
            coroutineScope.launch {
                viewModel.clearDeletedHealthTypes(e.id)
            }
        }

        //
        is Event.WillAddHealthRecordOfType -> {
            navController.navigate("addHealthRecord/${e.id}/${e.personId}")
        }

        is Event.ViewHealthRecordsOfType -> {
            navController.navigate(
                "healthRecords/${e.id}/${e.personId}" +
                        "/${e.filter.startDate}/${e.filter.endDate}"
            ) {
                // Note: 记录过滤采用的是路由跳转并附带过滤参数，
                // 因此，在退回时，需要直接退到初始路由上，避免逐级回退
                popUpTo("healthTypes/${e.personId}") {
                    inclusive = false
                }
            }
        }

        is Event.ViewHealthRecordDetailsOfType -> {
            navController.navigate("healthRecordDetails/${e.id}/${e.personId}")
        }

        is Event.ViewDeletedHealthRecordsOfType -> {
            navController.navigate("deletedHealthRecords/${e.id}/${e.personId}")
        }

        //
        is Event.WillEditHealthRecord -> {
            navController.navigate("editHealthRecord/${e.id}/${e.typeId}/${e.personId}")
        }

        is Event.SaveHealthRecord -> {
            coroutineScope.launch {
                viewModel.addHealthRecord(e.data)
                goback()
            }
        }

        is Event.UpdateHealthRecord -> {
            coroutineScope.launch {
                viewModel.updateHealthRecord(e.data)
                goback()
            }
        }

        is Event.DeleteHealthRecord -> {
            coroutineScope.launch {
                viewModel.deleteHealthRecord(e.id)
            }
        }

        is Event.UndeleteHealthRecord -> {
            coroutineScope.launch {
                viewModel.undeleteHealthRecord(e.id)
            }
        }

        is Event.ClearDeletedHealthRecordsOfType -> {
            coroutineScope.launch {
                viewModel.clearDeletedHealthRecords(e.id)
            }
        }
    }
}