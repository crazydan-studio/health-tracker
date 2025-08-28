// AppDatabase.kt
package org.crazydan.studio.app.healthtracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType

@Database(
    entities = [HealthType::class, HealthRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RangeListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthTypeDao(): HealthTypeDao
    abstract fun healthRecordDao(): HealthRecordDao
}