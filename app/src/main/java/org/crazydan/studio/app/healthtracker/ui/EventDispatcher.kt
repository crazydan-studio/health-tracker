package org.crazydan.studio.app.healthtracker.ui

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
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
            navController.navigate(
                Route.AddHealthPerson
            )
        }

        is Event.WillEditHealthPerson -> {
            navController.navigate(
                Route.EditHealthPerson(personId = e.id)
            )
        }

        is Event.ViewDeletedHealthPersons -> {
            navController.navigate(
                Route.DeletedHealthPersons
            )
        }

        is Event.SaveHealthPerson -> {
            coroutineScope.async {
                viewModel.addHealthPerson(e.data)
            }
            goback()
        }

        is Event.UpdateHealthPerson -> {
            coroutineScope.async {
                viewModel.updateHealthPerson(e.data)
            }
            goback()
        }

        is Event.DeleteHealthPerson -> {
            coroutineScope.async {
                viewModel.deleteHealthPerson(e.id)
            }
        }

        is Event.UndeleteHealthPerson -> {
            coroutineScope.async {
                viewModel.undeleteHealthPerson(e.id)
            }
        }

        is Event.ClearDeletedHealthPersons -> {
            coroutineScope.async {
                viewModel.clearDeletedHealthPersons()
            }
        }

        //
        is Event.WillAddHealthTypeOfPerson -> {
            navController.navigate(
                Route.AddHealthType(personId = e.personId)
            )
        }

        is Event.ViewHealthTypesOfPerson -> {
            navController.navigate(
                Route.HealthTypes(personId = e.personId)
            )
        }

        is Event.ViewDeletedHealthTypesOfPerson -> {
            navController.navigate(
                Route.DeletedHealthTypes(personId = e.personId)
            )
        }

        //
        is Event.WillEditHealthType -> {
            navController.navigate(
                Route.EditHealthType(
                    typeId = e.id,
                    personId = e.personId,
                )
            )
        }


        is Event.SaveHealthType -> {
            coroutineScope.async {
                viewModel.addHealthType(e.data)
            }
            goback()
        }

        is Event.UpdateHealthType -> {
            coroutineScope.async {
                viewModel.updateHealthType(e.data)
            }
            goback()
        }

        is Event.DeleteHealthType -> {
            coroutineScope.async {
                viewModel.deleteHealthType(e.id)
            }
        }

        is Event.UndeleteHealthType -> {
            coroutineScope.async {
                viewModel.undeleteHealthType(e.id)
            }
        }

        is Event.ClearDeletedHealthTypesOfPerson -> {
            coroutineScope.async {
                viewModel.clearDeletedHealthTypes(e.personId)
            }
        }

        //
        is Event.WillAddHealthRecordOfType -> {
            navController.navigate(
                Route.AddHealthRecord(
                    typeId = e.typeId,
                    personId = e.personId,
                )
            )
        }

        is Event.ViewHealthRecordsOfType -> {
            navController.navigate(
                Route.HealthRecords(
                    typeId = e.typeId,
                    personId = e.personId,
                    filter = e.filter,
                )
            ) {
                // Note: 记录过滤采用的是路由跳转并附带过滤参数，
                // 因此，在退回时，需要直接退到初始路由上，避免逐级回退
                popUpTo(
                    Route.HealthTypes(personId = e.personId)
                ) {
                    inclusive = false
                }
            }
        }

        is Event.ViewHealthRecordDetailsOfType -> {
            navController.navigate(
                Route.HealthRecordDetails(
                    typeId = e.typeId,
                    personId = e.personId,
                )
            )
        }

        is Event.ViewDeletedHealthRecordsOfType -> {
            navController.navigate(
                Route.DeletedHealthRecords(
                    typeId = e.typeId,
                    personId = e.personId,
                )
            )
        }

        //
        is Event.WillEditHealthRecord -> {
            navController.navigate(
                Route.EditHealthRecord(
                    recordId = e.id,
                    typeId = e.typeId,
                    personId = e.personId,
                )
            )
        }

        is Event.SaveHealthRecord -> {
            coroutineScope.async {
                viewModel.addHealthRecord(e.data)
            }
            goback()
        }

        is Event.UpdateHealthRecord -> {
            coroutineScope.async {
                viewModel.updateHealthRecord(e.data)
            }
            goback()
        }

        is Event.DeleteHealthRecord -> {
            coroutineScope.async {
                viewModel.deleteHealthRecord(e.id)
            }
        }

        is Event.UndeleteHealthRecord -> {
            coroutineScope.async {
                viewModel.undeleteHealthRecord(e.id)
            }
        }

        is Event.ClearDeletedHealthRecordsOfType -> {
            coroutineScope.async {
                viewModel.clearDeletedHealthRecords(e.typeId)
            }
        }
    }
}