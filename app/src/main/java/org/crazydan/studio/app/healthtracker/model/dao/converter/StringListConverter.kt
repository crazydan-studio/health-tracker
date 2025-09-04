package org.crazydan.studio.app.healthtracker.model.dao.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-03
 */
class StringListConverter {
    private val gson = Gson()
    private val type = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun toJson(list: List<String>?): String {
        return gson.toJson(list, type)
    }

    @TypeConverter
    fun fromJson(json: String?): List<String> {
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            gson.fromJson(json, type)
        }
    }
}