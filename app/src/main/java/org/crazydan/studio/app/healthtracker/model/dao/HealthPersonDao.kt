package org.crazydan.studio.app.healthtracker.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.crazydan.studio.app.healthtracker.model.HEALTH_PERSON_TABLE_NAME
import org.crazydan.studio.app.healthtracker.model.HealthPerson

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Dao
interface HealthPersonDao {
    @Insert
    suspend fun insert(healthPerson: HealthPerson): Long

    @Update
    suspend fun update(healthPerson: HealthPerson)

    @Query("DELETE FROM $HEALTH_PERSON_TABLE_NAME WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM $HEALTH_PERSON_TABLE_NAME ORDER BY familyName, givenName")
    fun getAll(): Flow<List<HealthPerson>>

    @Query("SELECT * FROM $HEALTH_PERSON_TABLE_NAME WHERE id = :id")
    fun getById(id: Long): Flow<HealthPerson?>
}