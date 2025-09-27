package org.crazydan.studio.app.healthtracker.ui

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import org.crazydan.studio.app.healthtracker.model.HealthRecordFilter
import org.crazydan.studio.app.healthtracker.model.HealthViewModel
import org.crazydan.studio.app.healthtracker.util.epochMillisToLocalDate
import org.crazydan.studio.app.healthtracker.util.toEpochMillis
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min

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
    fun goback(extras: Map<String, Any>? = null) {
        navController.popBackStack()

        // 向回退的目标路由附加额外参数
        extras?.forEach { (key, value) ->
            // Note: currentBackStackEntry.arguments 为 immutableArgs 的副本，对其修改是无效的
            navController.currentBackStackEntry?.savedStateHandle?.set(key, value)
        }
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
            coroutineScope.async {
                dispatchViewHealthRecordsOfType(
                    msg = msg,
                    viewModel = viewModel,
                    navController = navController,
                )
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

            var extras: Map<String, Any>? = null
            // Note: 仅针对新增数据附加过滤查询所需要包含的日期
            if (msg.data.id == 0L) {
                val date = epochMillisToLocalDate(msg.data.timestamp)
                extras = mapOf(
                    "filterIncludedDate" to toEpochMillis(date!!)
                )
            }
            goback(extras)
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

private suspend fun dispatchViewHealthRecordsOfType(
    msg: Message.ViewHealthRecordsOfType,
    viewModel: HealthViewModel,
    navController: NavController,
) {
    var filter: HealthRecordFilter? = msg.filter

    if (msg.latest7Days) {
        val result = viewModel.getHealthRecordLatest7DaysFilter(msg.typeId).first()

        val now = LocalDate.now()
        var start = toEpochMillis(now.minusDays(7))
        var end = toEpochMillis(now, untilToDayEnd = true)

        if (result.startDate > 0) {
            start = min(result.startDate, start)
        }
        if (result.endDate > 0) {
            end = max(result.endDate, end)
        }

        filter = HealthRecordFilter(
            startDate = start,
            endDate = end,
        )
    }

    navController.navigate(
        Route.HealthRecords(
            typeId = msg.typeId,
            personId = msg.personId,
            filter = filter!!,
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