package org.crazydan.studio.app.healthtracker.model.dao

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.crazydan.studio.app.healthtracker.model.NormalRange

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
class NormalRangeListConverter {
    private val gson = Gson()
    private val type = object : TypeToken<List<NormalRange>>() {}.type

    @TypeConverter
    fun fromRangeList(ranges: List<NormalRange>?): String {
        return gson.toJson(ranges, type)
    }

    @TypeConverter
    fun toRangeList(json: String?): List<NormalRange> {
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            gson.fromJson(json, type)
        }
    }
}