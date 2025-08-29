package org.crazydan.studio.app.healthtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
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
    // Health Persons
    private val _healthPersons = MutableStateFlow<List<HealthPerson>>(emptyList())
    val healthPersons: StateFlow<List<HealthPerson>> = _healthPersons.asStateFlow()

    private val _selectedHealthPerson = MutableStateFlow<HealthPerson?>(null)
    val selectedHealthPerson: StateFlow<HealthPerson?> = _selectedHealthPerson.asStateFlow()

    // Health Types
    private val _healthTypes = MutableStateFlow<List<HealthType>>(emptyList())
    val healthTypes: StateFlow<List<HealthType>> = _healthTypes.asStateFlow()

    private val _selectedHealthType = MutableStateFlow<HealthType?>(null)
    val selectedHealthType: StateFlow<HealthType?> = _selectedHealthType.asStateFlow()

    // Health Records
    private val _healthRecords = MutableStateFlow<List<HealthRecord>>(emptyList())
    val healthRecords: StateFlow<List<HealthRecord>> = _healthRecords.asStateFlow()

    private val _selectedRange = MutableStateFlow("")
    val selectedRange: StateFlow<String> = _selectedRange.asStateFlow()

    init {
        loadHealthPersons()
    }

    private fun loadHealthPersons() {
        viewModelScope.launch {
            repository.getAllHealthPersons().collectLatest { persons ->
                _healthPersons.value = persons
            }
        }
    }

    fun selectHealthPerson(person: HealthPerson?) {
        _selectedHealthPerson.value = person
        person?.let {
            loadHealthTypes(it.id)
        }
    }

    private fun loadHealthTypes(personId: Long) {
        viewModelScope.launch {
            repository.getHealthTypesByPersonId(personId).collectLatest { types ->
                _healthTypes.value = types
            }
        }
    }

    fun selectHealthType(type: HealthType?) {
        _selectedHealthType.value = type
        type?.let {
            loadHealthRecords(it.id)
        }
    }

    private fun loadHealthRecords(typeId: Long) {
        viewModelScope.launch {
            when {
                _selectedRange.value.isNotEmpty() -> {
                }

                else -> {
                    repository.getHealthRecordsByType(typeId).collectLatest { records ->
                        _healthRecords.value = records
                    }
                }
            }
        }
    }

    suspend fun addHealthPerson(healthPerson: HealthPerson): Long {
        return repository.insertHealthPerson(healthPerson)
    }

    suspend fun addHealthType(healthType: HealthType): Long {
        return repository.insertHealthType(healthType)
    }

    suspend fun updateHealthType(healthType: HealthType) {
        repository.updateHealthType(healthType)
    }

    suspend fun deleteHealthType(id: Long) {
        repository.deleteHealthType(id)
    }

    suspend fun addHealthRecord(healthRecord: HealthRecord): Long {
        return repository.insertHealthRecord(healthRecord)
    }
}