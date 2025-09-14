package org.crazydan.studio.app.healthtracker.model.dao.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import org.crazydan.studio.app.healthtracker.model.HealthMeasure

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
class HealthMeasureListConverter {
    private val json = Json {}

    @TypeConverter
    fun toJson(value: List<HealthMeasure>?): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun fromJson(value: String?): List<HealthMeasure> {
        return if (value.isNullOrEmpty()) {
            emptyList()
        } else {
            json.decodeFromString(value)
        }
    }
}