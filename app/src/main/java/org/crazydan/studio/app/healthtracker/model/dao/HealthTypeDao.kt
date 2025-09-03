package org.crazydan.studio.app.healthtracker.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.crazydan.studio.app.healthtracker.model.HEALTH_RECORD_TABLE_NAME
import org.crazydan.studio.app.healthtracker.model.HEALTH_TYPE_TABLE_NAME
import org.crazydan.studio.app.healthtracker.model.HealthType

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Dao
interface HealthTypeDao {
    @Insert
    suspend fun insert(healthType: HealthType): Long

    @Update
    suspend fun update(healthType: HealthType)

    @Query("UPDATE $HEALTH_TYPE_TABLE_NAME SET deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE $HEALTH_TYPE_TABLE_NAME SET deleted = 0 WHERE id = :id")
    suspend fun undelete(id: Long)

    @Query("DELETE FROM $HEALTH_TYPE_TABLE_NAME WHERE id = :id")
    suspend fun forceDelete(id: Long)

    @Query("SELECT * FROM $HEALTH_TYPE_TABLE_NAME WHERE id = :id")
    fun getById(id: Long): Flow<HealthType?>

    @Query("SELECT * FROM $HEALTH_TYPE_TABLE_NAME WHERE personId = :personId AND deleted = 0 ORDER BY id asc")
    fun getByPersonId(personId: Long): Flow<List<HealthType>>

    @Query("SELECT * FROM $HEALTH_TYPE_TABLE_NAME WHERE personId = :personId AND deleted = 1 ORDER BY id asc")
    fun getDeletedByPersonId(personId: Long): Flow<List<HealthType>>

    @Query("SELECT count(id) FROM $HEALTH_TYPE_TABLE_NAME WHERE personId = :personId AND deleted = 1")
    fun countDeletedByPersonId(personId: Long): Flow<Long>

    @Query("DELETE FROM $HEALTH_TYPE_TABLE_NAME WHERE personId = :personId AND deleted = 1")
    suspend fun clearDeleted(personId: Long)

    @Query(
        "DELETE FROM $HEALTH_RECORD_TABLE_NAME" +
                " WHERE typeId in" +
                " (select id FROM $HEALTH_TYPE_TABLE_NAME WHERE personId = :personId AND deleted = 1)"
    )
    suspend fun clearRecordsOfDeleted(personId: Long)
}