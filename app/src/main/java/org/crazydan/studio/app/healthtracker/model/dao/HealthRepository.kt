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

    fun getAllHealthPersons(): Flow<List<HealthPerson>> = healthPersonDao.getAll()

    fun getHealthPersonById(id: Long): Flow<HealthPerson?> = healthPersonDao.getById(id)

    // HealthType operations
    suspend fun insertHealthType(healthType: HealthType): Long = healthTypeDao.insert(healthType)

    suspend fun updateHealthType(healthType: HealthType) = healthTypeDao.update(healthType)

    suspend fun deleteHealthType(id: Long) = healthTypeDao.delete(id)

    fun getAllHealthTypes(): Flow<List<HealthType>> = healthTypeDao.getAll()

    fun getHealthTypeById(id: Long): Flow<HealthType?> = healthTypeDao.getById(id)

    fun getHealthTypesByPersonId(personId: Long): Flow<List<HealthType>> = healthTypeDao.getByPersonId(personId)

    // HealthRecord operations
    suspend fun insertHealthRecord(healthRecord: HealthRecord): Long = healthRecordDao.insert(healthRecord)

    suspend fun updateHealthRecord(healthRecord: HealthRecord) = healthRecordDao.update(healthRecord)

    suspend fun deleteHealthRecord(id: Long) = healthRecordDao.delete(id)

    fun getHealthRecordsByType(typeId: Long): Flow<List<HealthRecord>> = healthRecordDao.getByType(typeId)
}