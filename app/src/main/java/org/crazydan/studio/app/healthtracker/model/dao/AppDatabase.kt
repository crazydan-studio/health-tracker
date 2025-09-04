package org.crazydan.studio.app.healthtracker.model.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.crazydan.studio.app.healthtracker.model.HealthPerson
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.model.dao.converter.HealthLimitConverter
import org.crazydan.studio.app.healthtracker.model.dao.converter.HealthMeasureListConverter
import org.crazydan.studio.app.healthtracker.model.dao.converter.StringListConverter

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Database(
    entities = [
        HealthPerson::class, HealthType::class,
        HealthRecord::class,
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(
    HealthMeasureListConverter::class,
    HealthLimitConverter::class,
    StringListConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthPersonDao(): HealthPersonDao
    abstract fun healthTypeDao(): HealthTypeDao
    abstract fun healthRecordDao(): HealthRecordDao
}