package org.crazydan.studio.app.healthtracker.model.dao

import kotlinx.coroutines.flow.Flow
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
class HealthRepository(
    private val healthTypeDao: HealthTypeDao,
    private val healthRecordDao: HealthRecordDao
) {
    // HealthType operations
    suspend fun insertHealthType(healthType: HealthType): Long = healthTypeDao.insert(healthType)

    suspend fun updateHealthType(healthType: HealthType) = healthTypeDao.update(healthType)

    suspend fun deleteHealthType(id: Long) = healthTypeDao.delete(id)

    fun getAllHealthTypes(): Flow<List<HealthType>> = healthTypeDao.getAll()

    fun getHealthTypeById(id: Long): Flow<HealthType?> = healthTypeDao.getById(id)

    // HealthRecord operations
    suspend fun insertHealthRecord(healthRecord: HealthRecord): Long = healthRecordDao.insert(healthRecord)

    fun getHealthRecordsByType(typeId: Long): Flow<List<HealthRecord>> = healthRecordDao.getByType(typeId)

    fun getHealthRecordsByTypeAndPerson(typeId: Long, person: String): Flow<List<HealthRecord>> =
        healthRecordDao.getByTypeAndPerson(typeId, person)

    fun getHealthRecordsByTypeAndRange(typeId: Long, rangeName: String): Flow<List<HealthRecord>> =
        healthRecordDao.getByTypeAndRange(typeId, rangeName)

    fun getPersonsByType(typeId: Long): Flow<List<String>> = healthRecordDao.getPersonsByType(typeId)

    fun getRangesByType(typeId: Long): Flow<List<String>> = healthRecordDao.getRangesByType(typeId)
}