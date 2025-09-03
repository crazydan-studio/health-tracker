package org.crazydan.studio.app.healthtracker.model.dao

import kotlinx.coroutines.flow.Flow
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
class HealthRepository(
    private val healthPersonDao: HealthPersonDao,
    private val healthTypeDao: HealthTypeDao,
    private val healthRecordDao: HealthRecordDao,
) {
    // HealthPerson operations
    suspend fun insertHealthPerson(healthPerson: HealthPerson): Long = healthPersonDao.insert(healthPerson)

    suspend fun updateHealthPerson(healthPerson: HealthPerson) = healthPersonDao.update(healthPerson)

    suspend fun deleteHealthPerson(id: Long) = healthPersonDao.delete(id)

    suspend fun undeleteHealthPerson(id: Long) = healthPersonDao.undelete(id)

    fun getHealthPersonById(id: Long): Flow<HealthPerson?> = healthPersonDao.getById(id)

    fun getAllHealthPersons(): Flow<List<HealthPerson>> = healthPersonDao.getAll()

    fun getDeletedHealthPersons(): Flow<List<HealthPerson>> = healthPersonDao.getDeleted()

    fun countDeletedHealthPersons(): Flow<Long> = healthPersonDao.countDeleted()

    suspend fun clearDeletedHealthPersons() = healthPersonDao.clearDeleted()

    suspend fun clearHealthTypesOfDeletedHealthPersons() = healthPersonDao.clearTypesOfDeleted()

    suspend fun clearHealthRecordsOfDeletedHealthPersons() = healthPersonDao.clearRecordsOfDeleted()

    // HealthType operations
    suspend fun insertHealthType(healthType: HealthType): Long = healthTypeDao.insert(healthType)

    suspend fun updateHealthType(healthType: HealthType) = healthTypeDao.update(healthType)

    suspend fun deleteHealthType(id: Long) = healthTypeDao.delete(id)

    suspend fun undeleteHealthType(id: Long) = healthTypeDao.undelete(id)

    fun getHealthTypeById(id: Long): Flow<HealthType?> = healthTypeDao.getById(id)

    fun getHealthTypesByPersonId(personId: Long): Flow<List<HealthType>> = healthTypeDao.getByPersonId(personId)

    fun getDeletedHealthTypesByPersonId(personId: Long): Flow<List<HealthType>> =
        healthTypeDao.getDeletedByPersonId(personId)

    fun countDeletedHealthTypesByPersonId(personId: Long): Flow<Long> = healthTypeDao.countDeletedByPersonId(personId)

    suspend fun clearDeletedHealthTypes(personId: Long) = healthTypeDao.clearDeleted(personId)

    suspend fun clearHealthRecordsOfDeletedHealthTypes(personId: Long) = healthTypeDao.clearRecordsOfDeleted(personId)

    // HealthRecord operations
    suspend fun insertHealthRecord(healthRecord: HealthRecord): Long = healthRecordDao.insert(healthRecord)

    suspend fun updateHealthRecord(healthRecord: HealthRecord) = healthRecordDao.update(healthRecord)

    suspend fun deleteHealthRecord(id: Long) = healthRecordDao.delete(id)

    suspend fun undeleteHealthRecord(id: Long) = healthRecordDao.undelete(id)

    fun getHealthRecordById(id: Long): Flow<HealthRecord?> = healthRecordDao.getById(id)

    fun getHealthRecordsByTypeId(typeId: Long): Flow<List<HealthRecord>> = healthRecordDao.getByTypeId(typeId)

    fun getHealthRecordNotesByTypeId(typeId: Long): Flow<List<String>> = healthRecordDao.getNotesByTypeId(typeId)

    fun getDeletedHealthRecordsByTypeId(typeId: Long): Flow<List<HealthRecord>> =
        healthRecordDao.getDeletedByTypeId(typeId)

    fun countDeletedHealthRecordsByTypeId(typeId: Long): Flow<Long> = healthRecordDao.countDeletedByTypeId(typeId)

    suspend fun clearDeletedHealthRecords(typeId: Long) = healthRecordDao.clearDeleted(typeId)
}