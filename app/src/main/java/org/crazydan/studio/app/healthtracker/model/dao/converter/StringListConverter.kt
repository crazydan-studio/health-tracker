package org.crazydan.studio.app.healthtracker.model.dao.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-03
 */
class StringListConverter {
    private val json = Json {}

    @TypeConverter
    fun toJson(value: List<String>?): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun fromJson(value: String?): List<String> {
        return if (value.isNullOrEmpty()) {
            emptyList()
        } else {
            json.decodeFromString(value)
        }
    }
}