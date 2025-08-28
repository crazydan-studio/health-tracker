// HealthTypeDao.kt
package org.crazydan.studio.app.healthtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.crazydan.studio.app.healthtracker.model.HealthType

@Dao
interface HealthTypeDao {
    @Insert
    suspend fun insert(healthType: HealthType): Long

    @Update
    suspend fun update(healthType: HealthType)

    @Query("DELETE FROM health_types WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM health_types ORDER BY name")
    fun getAll(): Flow<List<HealthType>>

    @Query("SELECT * FROM health_types WHERE id = :id")
    fun getById(id: Long): Flow<HealthType?>
}