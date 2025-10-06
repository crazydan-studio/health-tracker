package org.crazydan.studio.app.healthtracker.ui

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.crazydan.studio.app.healthtracker.model.HealthRecordFilter

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-14
 */
sealed class Route {

    // <<<<<<<<<<< HealthPerson
    @Serializable
    data object HealthPersons : Route()

    @Serializable
    data object AddHealthPerson : Route()

    @Serializable
    data object DeletedHealthPersons : Route()

    @Serializable
    data class EditHealthPerson(val personId: Long) : Route()

    // >>>>>>>>>>>>

    // <<<<<<<<<<< HealthType
    @Serializable
    data class HealthTypes(val personId: Long) : Route()

    @Serializable
    data class AddHealthType(val personId: Long) : Route()

    @Serializable
    data class DeletedHealthTypes(val personId: Long) : Route()

    @Serializable
    data class EditHealthType(
        val typeId: Long, val personId: Long,
    ) : Route()

    // >>>>>>>>>>>>

    // <<<<<<<<<<< HealthRecord
    @Serializable
    data class HealthRecords(
        val typeId: Long, val personId: Long,
        val filter: HealthRecordFilter,
    ) : Route()

    @Serializable
    data class AddHealthRecord(
        val typeId: Long, val personId: Long,
    ) : Route()

    @Serializable
    data class EditHealthRecord(
        val recordId: Long,
        val typeId: Long, val personId: Long,
    ) : Route()

    @Serializable
    data class HealthRecordDetails(
        val typeId: Long, val personId: Long,
    ) : Route()

    @Serializable
    data class DeletedHealthRecords(
        val typeId: Long, val personId: Long,
    ) : Route()

    // >>>>>>>>>>>>
}

// <<<<<<<<<<<< Serializer
object HealthRecordFilterNavType : NavType<HealthRecordFilter>(isNullableAllowed = false) {
    private val json = Json { }

    // <<<<<<<<<<< 对路由对象进行序列化和反序列化
    override fun serializeAsValue(value: HealthRecordFilter): String {
        return json.encodeToString(value)
    }

    override fun parseValue(value: String): HealthRecordFilter {
        return json.decodeFromString(value)
    }
    // >>>>>>>>>>>>

    // <<<<<<<<<<<< 将路由对象暂存至 Bundle
    override fun put(bundle: SavedState, key: String, value: HealthRecordFilter) {
        bundle.putString(key, serializeAsValue(value))
    }

    override fun get(bundle: SavedState, key: String): HealthRecordFilter? {
        val s = bundle.getString(key)
        return s?.let { parseValue(it) }
    }
    // >>>>>>>>>>>>
}
// >>>>>>>>>>>>