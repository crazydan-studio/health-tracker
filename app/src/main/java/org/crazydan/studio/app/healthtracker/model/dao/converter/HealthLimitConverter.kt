package org.crazydan.studio.app.healthtracker.model.dao.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import org.crazydan.studio.app.healthtracker.model.HealthLimit

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-03
 */
class HealthLimitConverter {
    private val json = Json {}

    @TypeConverter
    fun toJson(value: HealthLimit?): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun fromJson(value: String?): HealthLimit {
        return if (value.isNullOrEmpty()) {
            HealthLimit()
        } else {
            json.decodeFromString(value)
        }
    }
}