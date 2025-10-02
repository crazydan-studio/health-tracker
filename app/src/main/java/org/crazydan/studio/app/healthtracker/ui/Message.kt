package org.crazydan.studio.app.healthtracker.ui

import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthRecordFilter
import org.crazydan.studio.app.healthtracker.model.HealthType

/**
 * https://kotlinlang.org/docs/sealed-classes.html#use-sealed-classes-with-when-expression
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-01
 */
sealed class Message {
    data object NavBack : Message()

    // Sync
    data object WillSyncHealthData : Message()

    // HealthPerson
    data object WillAddHealthPerson : Message()
    data class WillEditHealthPerson(val id: Long) : Message()
    data class SaveHealthPerson(val data: HealthPerson) : Message()
    data class DeleteHealthPerson(val id: Long) : Message()
    data class UndeleteHealthPerson(val id: Long) : Message()
    data object ViewDeletedHealthPersons : Message()
    data object ClearDeletedHealthPersons : Message()

    data class WillAddHealthTypeOfPerson(val personId: Long) : Message()
    data class ViewHealthTypesOfPerson(val personId: Long) : Message()
    data class ViewDeletedHealthTypesOfPerson(val personId: Long) : Message()
    data class ClearDeletedHealthTypesOfPerson(val personId: Long) : Message()

    // HealthType
    data class WillEditHealthType(val id: Long, val personId: Long) : Message()
    data class SaveHealthType(val data: HealthType) : Message()
    data class DeleteHealthType(val id: Long) : Message()
    data class UndeleteHealthType(val id: Long) : Message()

    data class WillAddHealthRecordOfType(
        val typeId: Long, val personId: Long
    ) : Message()

    data class ViewHealthRecordsOfType(
        val typeId: Long, val personId: Long,
        val filter: HealthRecordFilter? = null,
        /** 查看最近 7 天的数据 */
        val latest7Days: Boolean = false,
    ) : Message()

    data class ViewHealthRecordDetailsOfType(val typeId: Long, val personId: Long) : Message()
    data class ViewDeletedHealthRecordsOfType(val typeId: Long, val personId: Long) : Message()
    data class ClearDeletedHealthRecordsOfType(val typeId: Long) : Message()

    // HealthRecord
    data class WillEditHealthRecord(
        val id: Long,
        val typeId: Long, val personId: Long,
    ) : Message()

    data class SaveHealthRecord(val data: HealthRecord) : Message()
    data class DeleteHealthRecord(val id: Long) : Message()
    data class UndeleteHealthRecord(val id: Long) : Message()
}