package org.crazydan.studio.app.healthtracker.model.dao.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.crazydan.studio.app.healthtracker.model.HealthLimit

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-03
 */
class HealthLimitConverter {
    private val gson = Gson()
    private val type = object : TypeToken<HealthLimit>() {}.type

    @TypeConverter
    fun toJson(limit: HealthLimit?): String {
        return gson.toJson(limit, type)
    }

    @TypeConverter
    fun fromJson(json: String?): HealthLimit {
        return if (json.isNullOrEmpty()) {
            HealthLimit()
        } else {
            gson.fromJson(json, type)
        }
    }
}