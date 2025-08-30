package org.crazydan.studio.app.healthtracker.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
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

    // TODO 更新 HealthRecord#rangeName
    @Update
    suspend fun update(healthType: HealthType)

    @Query("UPDATE $HEALTH_TYPE_TABLE_NAME SET deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE $HEALTH_TYPE_TABLE_NAME SET deleted = 0 WHERE id = :id")
    suspend fun undelete(id: Long)

    @Query("DELETE FROM $HEALTH_TYPE_TABLE_NAME WHERE id = :id")
    suspend fun forceDelete(id: Long)

    @Query("SELECT * FROM $HEALTH_TYPE_TABLE_NAME WHERE deleted = 0 ORDER BY name")
    fun getAll(): Flow<List<HealthType>>

    @Query("SELECT * FROM $HEALTH_TYPE_TABLE_NAME WHERE personId = :personId AND deleted = 0 ORDER BY name")
    fun getByPersonId(personId: Long): Flow<List<HealthType>>

    @Query("SELECT * FROM $HEALTH_TYPE_TABLE_NAME WHERE id = :id")
    fun getById(id: Long): Flow<HealthType?>
}