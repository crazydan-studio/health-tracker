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
fun dispatchMessage(
    msg: Message,
    viewModel: HealthViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope,
) {
    val goback = fun() {
        navController.popBackStack()
    }

    when (msg) {
        is Message.NavBack -> {
            goback()
        }

        is Message.WillAddHealthPerson -> {
            navController.navigate(
                Route.AddHealthPerson
            )
        }

        is Message.WillEditHealthPerson -> {
            navController.navigate(
                Route.EditHealthPerson(personId = msg.id)
            )
        }

        is Message.ViewDeletedHealthPersons -> {
            navController.navigate(
                Route.DeletedHealthPersons
            )
        }

        is Message.SaveHealthPerson -> {
            coroutineScope.async {
                viewModel.saveHealthPerson(msg.data)
            }
            goback()
        }

        is Message.DeleteHealthPerson -> {
            coroutineScope.async {
                viewModel.deleteHealthPerson(msg.id)
            }
        }

        is Message.UndeleteHealthPerson -> {
            coroutineScope.async {
                viewModel.undeleteHealthPerson(msg.id)
            }
        }

        is Message.ClearDeletedHealthPersons -> {
            coroutineScope.async {
                viewModel.clearDeletedHealthPersons()
            }
        }

        //
        is Message.WillAddHealthTypeOfPerson -> {
            navController.navigate(
                Route.AddHealthType(personId = msg.personId)
            )
        }

        is Message.ViewHealthTypesOfPerson -> {
            navController.navigate(
                Route.HealthTypes(personId = msg.personId)
            )
        }

        is Message.ViewDeletedHealthTypesOfPerson -> {
            navController.navigate(
                Route.DeletedHealthTypes(personId = msg.personId)
            )
        }

        //
        is Message.WillEditHealthType -> {
            navController.navigate(
                Route.EditHealthType(
                    typeId = msg.id,
                    personId = msg.personId,
                )
            )
        }

        is Message.SaveHealthType -> {
            coroutineScope.async {
                viewModel.saveHealthType(msg.data)
            }
            goback()
        }

        is Message.DeleteHealthType -> {
            coroutineScope.async {
                viewModel.deleteHealthType(msg.id)
            }
        }

        is Message.UndeleteHealthType -> {
            coroutineScope.async {
                viewModel.undeleteHealthType(msg.id)
            }
        }

        is Message.ClearDeletedHealthTypesOfPerson -> {
            coroutineScope.async {
                viewModel.clearDeletedHealthTypes(msg.personId)
            }
        }

        //
        is Message.WillAddHealthRecordOfType -> {
            navController.navigate(
                Route.AddHealthRecord(
                    typeId = msg.typeId,
                    personId = msg.personId,
                )
            )
        }

        is Message.ViewHealthRecordsOfType -> {
            navController.navigate(
                Route.HealthRecords(
                    typeId = msg.typeId,
                    personId = msg.personId,
                    filter = msg.filter,
                )
            ) {
                // Note: 记录过滤采用的是路由跳转并附带过滤参数，
                // 因此，在退回时，需要直接退到初始路由上，避免逐级回退
                popUpTo(
                    Route.HealthTypes(personId = msg.personId)
                ) {
                    inclusive = false
                }
            }
        }

        is Message.ViewHealthRecordDetailsOfType -> {
            navController.navigate(
                Route.HealthRecordDetails(
                    typeId = msg.typeId,
                    personId = msg.personId,
                )
            )
        }

        is Message.ViewDeletedHealthRecordsOfType -> {
            navController.navigate(
                Route.DeletedHealthRecords(
                    typeId = msg.typeId,
                    personId = msg.personId,
                )
            )
        }

        //
        is Message.WillEditHealthRecord -> {
            navController.navigate(
                Route.EditHealthRecord(
                    recordId = msg.id,
                    typeId = msg.typeId,
                    personId = msg.personId,
                )
            )
        }

        is Message.SaveHealthRecord -> {
            coroutineScope.async {
                viewModel.saveHealthRecord(msg.data)
            }
            goback()
        }

        is Message.DeleteHealthRecord -> {
            coroutineScope.async {
                viewModel.deleteHealthRecord(msg.id)
            }
        }

        is Message.UndeleteHealthRecord -> {
            coroutineScope.async {
                viewModel.undeleteHealthRecord(msg.id)
            }
        }

        is Message.ClearDeletedHealthRecordsOfType -> {
            coroutineScope.async {
                viewModel.clearDeletedHealthRecords(msg.typeId)
            }
        }
    }
}