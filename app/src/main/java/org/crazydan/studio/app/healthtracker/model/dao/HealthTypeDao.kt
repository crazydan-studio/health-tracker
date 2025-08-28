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

    @Update
    suspend fun update(healthType: HealthType)

    @Query("DELETE FROM $HEALTH_TYPE_TABLE_NAME WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM $HEALTH_TYPE_TABLE_NAME ORDER BY name")
    fun getAll(): Flow<List<HealthType>>

    @Query("SELECT * FROM $HEALTH_TYPE_TABLE_NAME WHERE id = :id")
    fun getById(id: Long): Flow<HealthType?>
}