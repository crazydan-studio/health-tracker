package org.crazydan.studio.app.healthtracker.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.crazydan.studio.app.healthtracker.model.HEALTH_RECORD_TABLE_NAME
import org.crazydan.studio.app.healthtracker.model.HealthRecord

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Dao
interface HealthRecordDao {
    @Insert
    suspend fun insert(healthRecord: HealthRecord): Long

    @Query("SELECT * FROM $HEALTH_RECORD_TABLE_NAME WHERE typeId = :typeId ORDER BY timestamp DESC")
    fun getByType(typeId: Long): Flow<List<HealthRecord>>

    @Query("SELECT * FROM $HEALTH_RECORD_TABLE_NAME WHERE typeId = :typeId AND person = :person ORDER BY timestamp DESC")
    fun getByTypeAndPerson(typeId: Long, person: String): Flow<List<HealthRecord>>

    @Query("SELECT * FROM $HEALTH_RECORD_TABLE_NAME WHERE typeId = :typeId AND rangeName = :rangeName ORDER BY timestamp DESC")
    fun getByTypeAndRange(typeId: Long, rangeName: String): Flow<List<HealthRecord>>

    @Query("SELECT DISTINCT person FROM $HEALTH_RECORD_TABLE_NAME WHERE typeId = :typeId")
    fun getPersonsByType(typeId: Long): Flow<List<String>>

    @Query("SELECT DISTINCT rangeName FROM $HEALTH_RECORD_TABLE_NAME WHERE typeId = :typeId")
    fun getRangesByType(typeId: Long): Flow<List<String>>
}