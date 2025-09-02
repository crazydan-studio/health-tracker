package org.crazydan.studio.app.healthtracker.ui

import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType

/**
 * https://kotlinlang.org/docs/sealed-classes.html#use-sealed-classes-with-when-expression
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-01
 */
sealed class Event {
    class NavBack() : Event()

    class WillAddHealthPerson() : Event()
    class WillEditHealthPerson(val id: Long) : Event()
    class SaveHealthPerson(val data: HealthPerson) : Event()
    class UpdateHealthPerson(val data: HealthPerson) : Event()
    class DeleteHealthPerson(val id: Long) : Event()
    class UndeleteHealthPerson(val id: Long) : Event()
    class ViewDeletedHealthPersons() : Event()
    class ClearDeletedHealthPersons() : Event()

    class WillAddHealthTypeOfPerson(val id: Long) : Event()
    class ViewHealthTypesOfPerson(val id: Long) : Event()
    class ViewDeletedHealthTypesOfPerson(val id: Long) : Event()
    class ClearDeletedHealthTypesOfPerson(val id: Long) : Event()

    class WillEditHealthType(val id: Long, val personId: Long) : Event()
    class SaveHealthType(val data: HealthType) : Event()
    class UpdateHealthType(val data: HealthType) : Event()
    class DeleteHealthType(val id: Long) : Event()
    class UndeleteHealthType(val id: Long) : Event()

    class WillAddHealthRecordOfType(val id: Long, val personId: Long) : Event()
    class ViewHealthRecordsOfType(val id: Long, val personId: Long) : Event()
    class ViewHealthRecordDetailsOfType(val id: Long, val personId: Long) : Event()
    class ViewDeletedHealthRecordsOfType(val id: Long, val personId: Long) : Event()
    class ClearDeletedHealthRecordsOfType(val id: Long) : Event()

    class WillEditHealthRecord(val id: Long, val typeId: Long, val personId: Long) : Event()
    class SaveHealthRecord(val data: HealthRecord) : Event()
    class UpdateHealthRecord(val data: HealthRecord) : Event()
    class DeleteHealthRecord(val id: Long) : Event()
    class UndeleteHealthRecord(val id: Long) : Event()
}

typealias EventDispatch = (Event) -> Unit
