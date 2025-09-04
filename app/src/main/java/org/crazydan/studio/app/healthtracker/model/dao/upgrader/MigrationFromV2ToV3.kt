package org.crazydan.studio.app.healthtracker.model.dao.upgrader

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.crazydan.studio.app.healthtracker.model.HEALTH_RECORD_TABLE_NAME
import org.crazydan.studio.app.healthtracker.model.HEALTH_TYPE_TABLE_NAME
import org.crazydan.studio.app.healthtracker.model.HealthLimit
import org.crazydan.studio.app.healthtracker.model.HealthMeasure
import org.crazydan.studio.app.healthtracker.model.genMeasureCode
import java.lang.reflect.Type

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-03
 */
class MigrationFromV2ToV3() : Migration(2, 3) {

    override fun migrate(db: SupportSQLiteDatabase) {
        // Note: 低版本 SQLite 不支持对列做修改、删除和重命名，只能重建表
        // alter table
        db.execSQL("alter table $HEALTH_TYPE_TABLE_NAME rename to type_")
        db.execSQL(
            "create table $HEALTH_TYPE_TABLE_NAME (" +
                    "`id` integer primary key autoincrement not null," +
                    "`deleted` integer not null," +
                    "`personId` integer not null," +
                    "`name` text not null," +
                    "`unit` text not null," +
                    "`limit` text not null," +
                    "`measures` text not null" +
                    ")"
        )
        db.execSQL(
            "insert into $HEALTH_TYPE_TABLE_NAME" +
                    " (id, deleted, personId, name, unit, `limit`, measures)" +
                    " select id, deleted, personId, name, unit, '{}', ranges" +
                    " from type_"
        )
        db.execSQL("drop table type_")

        // remove foreign key
        db.execSQL("alter table $HEALTH_RECORD_TABLE_NAME rename to record_")
        db.execSQL(
            "create table $HEALTH_RECORD_TABLE_NAME (" +
                    "`id` integer primary key autoincrement not null," +
                    "`deleted` integer not null," +
                    "`typeId` integer not null," +
                    "`personId` integer not null," +
                    "`value` real not null," +
                    "`timestamp` integer not null," +
                    "`measure` text not null," +
                    "`notes` text not null," +
                    "`createdAt` integer not null" +
                    ")"
        )
        db.execSQL(
            "insert into $HEALTH_RECORD_TABLE_NAME" +
                    " (id, deleted, typeId, personId, value, timestamp, measure, notes, createdAt)" +
                    " select id, deleted, typeId, personId, value, timestamp, rangeName, notes, createdAt" +
                    " from record_"
        )
        db.execSQL("drop table record_")

        // transfer data
        val typeMeasuresMap = transferTypeMeasures(db)
        transferRecordMeasureCodes(db, typeMeasuresMap)
    }
}

private fun transferTypeMeasures(db: SupportSQLiteDatabase): Map<Long, List<HealthMeasure>> {
    val gson = Gson()
    val rangeListType = object : TypeToken<List<OldRange>>() {}.type
    val typeMeasuresMap = mutableMapOf<Long, List<HealthMeasure>>()

    db.query("select id, measures from $HEALTH_TYPE_TABLE_NAME").also { cursor ->
        try {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                    val measures = jsonToMeasures(
                        gson, rangeListType,
                        cursor.getString(cursor.getColumnIndexOrThrow("measures"))
                    )

                    if (measures.isNotEmpty()) {
                        typeMeasuresMap.put(id, measures)
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
    }

    val statement = db.compileStatement("update $HEALTH_TYPE_TABLE_NAME set measures = ? where id = ?")
    try {
        typeMeasuresMap.forEach { id, measures ->
            statement.bindString(1, gson.toJson(measures))
            statement.bindLong(2, id)

            statement.executeUpdateDelete()
        }
    } finally {
        statement.close()
    }

    return typeMeasuresMap
}

private fun transferRecordMeasureCodes(db: SupportSQLiteDatabase, typeMeasuresMap: Map<Long, List<HealthMeasure>>) {
    val recordMeasureMap = mutableMapOf<Long, String>()

    db.query("select id, typeId, measure from $HEALTH_RECORD_TABLE_NAME where measure != ''").also { cursor ->
        try {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                    val typeId = cursor.getLong(cursor.getColumnIndexOrThrow("typeId"))
                    val measureName = cursor.getString(cursor.getColumnIndexOrThrow("measure"))

                    val measures = typeMeasuresMap[typeId]
                    val measureCode =
                        if (measures.isNullOrEmpty()) null
                        else measures.firstOrNull { it.name == measureName }?.code

                    recordMeasureMap.put(id, measureCode ?: "")
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
    }

    val statement = db.compileStatement("update $HEALTH_RECORD_TABLE_NAME set measure = ? where id = ?")
    try {
        recordMeasureMap.forEach { id, code ->
            statement.bindString(1, code)
            statement.bindLong(2, id)

            statement.executeUpdateDelete()
        }
    } finally {
        statement.close()
    }
}

private fun jsonToMeasures(gson: Gson, type: Type, json: String): List<HealthMeasure> {
    val ranges: List<OldRange> = gson.fromJson(json, type)

    return if (ranges.isEmpty()) {
        emptyList()
    } else {
        ranges.map {
            HealthMeasure(
                code = genMeasureCode(),
                name = it.name,
                limit = HealthLimit(
                    lower = it.lowerLimit,
                    upper = it.upperLimit,
                )
            )
        }
    }
}

data class OldRange(
    val name: String,
    val upperLimit: Float,
    val lowerLimit: Float,
)