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

    @Query("UPDATE $HEALTH_PERSON_TABLE_NAME SET deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE $HEALTH_PERSON_TABLE_NAME SET deleted = 0 WHERE id = :id")
    suspend fun undelete(id: Long)

    @Query("DELETE FROM $HEALTH_PERSON_TABLE_NAME WHERE id = :id")
    suspend fun forceDelete(id: Long)

    @Query("SELECT * FROM $HEALTH_PERSON_TABLE_NAME WHERE id = :id")
    fun getById(id: Long): Flow<HealthPerson?>

    @Query("SELECT * FROM $HEALTH_PERSON_TABLE_NAME WHERE deleted = 0 ORDER BY id asc")
    fun getAll(): Flow<List<HealthPerson>>

    @Query("SELECT * FROM $HEALTH_PERSON_TABLE_NAME WHERE deleted = 1 ORDER BY id asc")
    fun getDeleted(): Flow<List<HealthPerson>>

    @Query("SELECT count(id) FROM $HEALTH_PERSON_TABLE_NAME WHERE deleted = 1")
    fun countDeleted(): Flow<Long>

    @Query("DELETE FROM $HEALTH_PERSON_TABLE_NAME WHERE deleted = 1")
    suspend fun clearDeleted()
}