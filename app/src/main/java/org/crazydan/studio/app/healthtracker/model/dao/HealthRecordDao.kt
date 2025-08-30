package org.crazydan.studio.app.healthtracker.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

    @Update
    suspend fun update(healthRecord: HealthRecord)

    @Query("DELETE FROM $HEALTH_RECORD_TABLE_NAME WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM $HEALTH_RECORD_TABLE_NAME WHERE typeId = :typeId ORDER BY timestamp DESC")
    fun getByType(typeId: Long): Flow<List<HealthRecord>>
}