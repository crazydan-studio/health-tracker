package org.crazydan.studio.app.healthtracker.model.dao.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.crazydan.studio.app.healthtracker.model.HealthMeasure

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
class HealthMeasureListConverter {
    private val gson = Gson()
    private val type = object : TypeToken<List<HealthMeasure>>() {}.type

    @TypeConverter
    fun toJson(measures: List<HealthMeasure>?): String {
        return gson.toJson(measures, type)
    }

    @TypeConverter
    fun fromJson(json: String?): List<HealthMeasure> {
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            gson.fromJson(json, type)
        }
    }
}