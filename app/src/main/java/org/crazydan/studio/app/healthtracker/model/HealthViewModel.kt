package org.crazydan.studio.app.healthtracker.model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import org.crazydan.studio.app.healthtracker.model.dao.HealthRepository
import javax.inject.Inject

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@HiltViewModel
class HealthViewModel @Inject constructor(
    private val repository: HealthRepository
) : ViewModel() {

    fun getHealthPerson(id: Long): Flow<HealthPerson?> {
        return repository.getHealthPersonById(id)
    }

    fun getHealthPersons(): Flow<List<HealthPerson>> {
        return repository.getAllHealthPersons()
    }

    fun getDeletedHealthPersons(): Flow<List<HealthPerson>> {
        return repository.getDeletedHealthPersons()
    }

    suspend fun addHealthPerson(healthPerson: HealthPerson): Long {
        return repository.insertHealthPerson(healthPerson)
    }

    suspend fun updateHealthPerson(healthPerson: HealthPerson) {
        repository.updateHealthPerson(healthPerson)
    }

    fun countDeletedHealthPersons(): Flow<Long> {
        return repository.countDeletedHealthPersons()
    }

    suspend fun deleteHealthPerson(id: Long) {
        repository.deleteHealthPerson(id)
    }

    suspend fun undeleteHealthPerson(id: Long) {
        repository.undeleteHealthPerson(id)
    }

    suspend fun clearDeletedHealthPersons() {
        repository.clearHealthRecordsOfDeletedHealthPersons()
        repository.clearHealthTypesOfDeletedHealthPersons()

        repository.clearDeletedHealthPersons()
    }

    //
    fun getHealthType(id: Long): Flow<HealthType?> {
        return repository.getHealthTypeById(id)
    }

    fun getHealthTypes(personId: Long): Flow<List<HealthType>> {
        return repository.getHealthTypesByPersonId(personId)
    }

    fun getDeletedHealthTypes(personId: Long): Flow<List<HealthType>> {
        return repository.getDeletedHealthTypesByPersonId(personId)
    }

    suspend fun addHealthType(healthType: HealthType): Long {
        return repository.insertHealthType(healthType)
    }

    suspend fun updateHealthType(healthType: HealthType) {
        repository.updateHealthType(healthType)
    }

    fun countDeletedHealthTypes(personId: Long): Flow<Long> {
        return repository.countDeletedHealthTypesByPersonId(personId)
    }

    suspend fun deleteHealthType(id: Long) {
        repository.deleteHealthType(id)
    }

    suspend fun undeleteHealthType(id: Long) {
        repository.undeleteHealthType(id)
    }

    suspend fun clearDeletedHealthTypes(personId: Long) {
        repository.clearHealthRecordsOfDeletedHealthTypes(personId)

        repository.clearDeletedHealthTypes(personId)
    }

    //
    fun getHealthRecord(id: Long): Flow<HealthRecord?> {
        return repository.getHealthRecordById(id)
    }

    fun getHealthRecords(typeId: Long): Flow<List<HealthRecord>> {
        return repository.getHealthRecordsByTypeId(typeId)
    }

    fun getDeletedHealthRecords(typeId: Long): Flow<List<HealthRecord>> {
        return repository.getDeletedHealthRecordsByTypeId(typeId)
    }

    fun getHealthRecordTags(typeId: Long): Flow<List<String>> {
        return repository.getHealthRecordTagsByTypeId(typeId)
    }

    suspend fun addHealthRecord(healthRecord: HealthRecord): Long {
        return repository.insertHealthRecord(healthRecord)
    }

    suspend fun updateHealthRecord(healthRecord: HealthRecord) {
        repository.updateHealthRecord(healthRecord)
    }

    fun countDeletedHealthRecords(typeId: Long): Flow<Long> {
        return repository.countDeletedHealthRecordsByTypeId(typeId)
    }

    suspend fun deleteHealthRecord(id: Long) {
        repository.deleteHealthRecord(id)
    }

    suspend fun undeleteHealthRecord(id: Long) {
        repository.undeleteHealthRecord(id)
    }

    suspend fun clearDeletedHealthRecords(typeId: Long) {
        repository.clearDeletedHealthRecords(typeId)
    }
}