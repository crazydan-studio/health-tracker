// HealthRecordDao.kt
package org.crazydan.studio.app.healthtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.crazydan.studio.app.healthtracker.model.HealthRecord

@Dao
interface HealthRecordDao {
    @Insert
    suspend fun insert(healthRecord: HealthRecord): Long

    @Query("SELECT * FROM health_records WHERE typeId = :typeId ORDER BY timestamp DESC")
    fun getByType(typeId: Long): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE typeId = :typeId AND person = :person ORDER BY timestamp DESC")
    fun getByTypeAndPerson(typeId: Long, person: String): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE typeId = :typeId AND rangeName = :rangeName ORDER BY timestamp DESC")
    fun getByTypeAndRange(typeId: Long, rangeName: String): Flow<List<HealthRecord>>

    @Query("SELECT DISTINCT person FROM health_records WHERE typeId = :typeId")
    fun getPersonsByType(typeId: Long): Flow<List<String>>

    @Query("SELECT DISTINCT rangeName FROM health_records WHERE typeId = :typeId")
    fun getRangesByType(typeId: Long): Flow<List<String>>
}