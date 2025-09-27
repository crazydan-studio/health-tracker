package org.crazydan.studio.app.healthtracker.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.crazydan.studio.app.healthtracker.model.HEALTH_RECORD_TABLE_NAME
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthRecordFilter

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

    @Query("UPDATE $HEALTH_RECORD_TABLE_NAME SET deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE $HEALTH_RECORD_TABLE_NAME SET deleted = 0 WHERE id = :id")
    suspend fun undelete(id: Long)

    @Query("DELETE FROM $HEALTH_RECORD_TABLE_NAME WHERE id = :id")
    suspend fun forceDelete(id: Long)

    @Query("SELECT * FROM $HEALTH_RECORD_TABLE_NAME WHERE id = :id")
    fun getById(id: Long): Flow<HealthRecord?>

    @Query(
        """
        SELECT * FROM $HEALTH_RECORD_TABLE_NAME
        WHERE typeId = :typeId AND deleted = 0
            AND timestamp >= :startTimestamp
            AND timestamp <= :endTimestamp
        ORDER BY timestamp DESC
    """
    )
    fun getByTypeId(typeId: Long, startTimestamp: Long, endTimestamp: Long): Flow<List<HealthRecord>>

    @Query("SELECT * FROM $HEALTH_RECORD_TABLE_NAME WHERE typeId = :typeId AND deleted = 1 ORDER BY timestamp DESC")
    fun getDeletedByTypeId(typeId: Long): Flow<List<HealthRecord>>

    @Query("SELECT count(id) FROM $HEALTH_RECORD_TABLE_NAME WHERE typeId = :typeId AND deleted = 1")
    fun countDeletedByTypeId(typeId: Long): Flow<Long>

    @Query("DELETE FROM $HEALTH_RECORD_TABLE_NAME WHERE typeId = :typeId AND deleted = 1")
    suspend fun clearDeleted(typeId: Long)

    @Query("SELECT DISTINCT tags FROM $HEALTH_RECORD_TABLE_NAME WHERE typeId = :typeId")
    fun getTagsByTypeId(typeId: Long): Flow<List<String>>

    // Note: date 等函数是按 UTC 时区进行时间转换的，
    // 为降低复杂性，需尽可能返回数据时间本身，避免对结果做时间转换
    @Query(
        """
        SELECT
            min(t_) as startDate,
            max(t_) as endDate
        FROM (
            SELECT
                date(timestamp / 1000, 'unixepoch') AS d_
                , max(timestamp) AS t_
            FROM $HEALTH_RECORD_TABLE_NAME
            WHERE typeId = :typeId AND deleted = 0
            GROUP BY d_
            ORDER BY d_ DESC
            LIMIT 7
        )
    """
    )
    fun getLatest7DaysFilterByTypeId(typeId: Long): Flow<HealthRecordFilter>
}