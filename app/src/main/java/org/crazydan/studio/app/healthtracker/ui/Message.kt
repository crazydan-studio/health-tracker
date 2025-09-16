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
    class NavBack() : Message()

    // HealthPerson
    class WillAddHealthPerson() : Message()
    class WillEditHealthPerson(val id: Long) : Message()
    class SaveHealthPerson(val data: HealthPerson) : Message()
    class UpdateHealthPerson(val data: HealthPerson) : Message()
    class DeleteHealthPerson(val id: Long) : Message()
    class UndeleteHealthPerson(val id: Long) : Message()
    class ViewDeletedHealthPersons() : Message()
    class ClearDeletedHealthPersons() : Message()

    class WillAddHealthTypeOfPerson(val personId: Long) : Message()
    class ViewHealthTypesOfPerson(val personId: Long) : Message()
    class ViewDeletedHealthTypesOfPerson(val personId: Long) : Message()
    class ClearDeletedHealthTypesOfPerson(val personId: Long) : Message()

    // HealthType
    class WillEditHealthType(val id: Long, val personId: Long) : Message()
    class SaveHealthType(val data: HealthType) : Message()
    class UpdateHealthType(val data: HealthType) : Message()
    class DeleteHealthType(val id: Long) : Message()
    class UndeleteHealthType(val id: Long) : Message()

    class WillAddHealthRecordOfType(val typeId: Long, val personId: Long) : Message()
    class ViewHealthRecordsOfType(
        val typeId: Long, val personId: Long,
        val filter: HealthRecordFilter,
    ) : Message()

    class ViewHealthRecordDetailsOfType(val typeId: Long, val personId: Long) : Message()
    class ViewDeletedHealthRecordsOfType(val typeId: Long, val personId: Long) : Message()
    class ClearDeletedHealthRecordsOfType(val typeId: Long) : Message()

    // HealthRecord
    class WillEditHealthRecord(
        val id: Long,
        val typeId: Long, val personId: Long,
    ) : Message()

    class SaveHealthRecord(val data: HealthRecord) : Message()
    class UpdateHealthRecord(val data: HealthRecord) : Message()
    class DeleteHealthRecord(val id: Long) : Message()
    class UndeleteHealthRecord(val id: Long) : Message()
}