package org.crazydan.studio.app.healthtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.crazydan.studio.app.healthtracker.model.dao.HealthRepository
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
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
    // Health Types
    private val _healthTypes = MutableStateFlow<List<HealthType>>(emptyList())
    val healthTypes: StateFlow<List<HealthType>> = _healthTypes.asStateFlow()

    private val _selectedHealthType = MutableStateFlow<HealthType?>(null)
    val selectedHealthType: StateFlow<HealthType?> = _selectedHealthType.asStateFlow()

    // Health Records
    private val _healthRecords = MutableStateFlow<List<HealthRecord>>(emptyList())
    val healthRecords: StateFlow<List<HealthRecord>> = _healthRecords.asStateFlow()

    private val _persons = MutableStateFlow<List<String>>(emptyList())
    val persons: StateFlow<List<String>> = _persons.asStateFlow()

    private val _ranges = MutableStateFlow<List<String>>(emptyList())
    val ranges: StateFlow<List<String>> = _ranges.asStateFlow()

    private val _selectedPerson = MutableStateFlow("")
    val selectedPerson: StateFlow<String> = _selectedPerson.asStateFlow()

    private val _selectedRange = MutableStateFlow("")
    val selectedRange: StateFlow<String> = _selectedRange.asStateFlow()

    init {
        loadHealthTypes()
    }

    private fun loadHealthTypes() {
        viewModelScope.launch {
            repository.getAllHealthTypes().collectLatest { types ->
                _healthTypes.value = types
            }
        }
    }

    fun selectHealthType(type: HealthType?) {
        _selectedHealthType.value = type
        type?.let {
            loadHealthRecords(it.id)
            loadPersons(it.id)
            loadRanges(it.id)
        }
    }

    private fun loadHealthRecords(typeId: Long) {
        viewModelScope.launch {
            when {
                _selectedPerson.value.isNotEmpty() && _selectedRange.value.isNotEmpty() -> {
                    // 同时筛选人员和范围
                    // 这里需要根据实际情况实现
                    repository.getHealthRecordsByType(typeId).collectLatest { records ->
                        _healthRecords.value = records
                    }
                }

                _selectedPerson.value.isNotEmpty() -> {
                    repository.getHealthRecordsByTypeAndPerson(typeId, _selectedPerson.value)
                        .collectLatest { records ->
                            _healthRecords.value = records
                        }
                }

                _selectedRange.value.isNotEmpty() -> {
                    repository.getHealthRecordsByTypeAndRange(typeId, _selectedRange.value)
                        .collectLatest { records ->
                            _healthRecords.value = records
                        }
                }

                else -> {
                    repository.getHealthRecordsByType(typeId).collectLatest { records ->
                        _healthRecords.value = records
                    }
                }
            }
        }
    }

    private fun loadPersons(typeId: Long) {
        viewModelScope.launch {
            repository.getPersonsByType(typeId).collectLatest { persons ->
                _persons.value = persons
            }
        }
    }

    private fun loadRanges(typeId: Long) {
        viewModelScope.launch {
            repository.getRangesByType(typeId).collectLatest { ranges ->
                _ranges.value = ranges
            }
        }
    }

    fun selectPerson(person: String) {
        _selectedPerson.value = person
        _selectedHealthType.value?.let { loadHealthRecords(it.id) }
    }

    fun selectRange(range: String) {
        _selectedRange.value = range
        _selectedHealthType.value?.let { loadHealthRecords(it.id) }
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