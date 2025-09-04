package org.crazydan.studio.app.healthtracker.model.dao.upgrader

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.crazydan.studio.app.healthtracker.model.HEALTH_RECORD_TABLE_NAME
import org.crazydan.studio.app.healthtracker.model.dao.converter.StringListConverter

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-04
 */
class MigrationFromV3ToV4() : Migration(3, 4) {

    override fun migrate(db: SupportSQLiteDatabase) {
        // Note: 低版本 SQLite 不支持对列做修改、删除和重命名，只能重建表
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
                    "`tags` text not null," +
                    "`createdAt` integer not null" +
                    ")"
        )
        db.execSQL(
            "insert into $HEALTH_RECORD_TABLE_NAME" +
                    " (id, deleted, typeId, personId, value, timestamp, measure, tags, createdAt)" +
                    " select id, deleted, typeId, personId, value, timestamp, measure, notes, createdAt" +
                    " from record_"
        )
        db.execSQL("drop table record_")

        // transfer data
        transferRecordTags(db)
    }
}

private fun transferRecordTags(db: SupportSQLiteDatabase) {
    val recordTagsMap = mutableMapOf<Long, List<String>>()

    db.query("select id, tags from $HEALTH_RECORD_TABLE_NAME").also { cursor ->
        try {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                    val tags = cursor.getString(cursor.getColumnIndexOrThrow("tags"))

                    recordTagsMap.put(id, if (tags.isBlank()) emptyList() else listOf(tags))
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
    }

    val statement = db.compileStatement("update $HEALTH_RECORD_TABLE_NAME set tags = ? where id = ?")
    try {
        recordTagsMap.forEach { id, tags ->
            val json = StringListConverter().toJson(tags)

            statement.bindString(1, json)
            statement.bindLong(2, id)

            statement.executeUpdateDelete()
        }
    } finally {
        statement.close()
    }
}