package org.crazydan.studio.app.healthtracker.model.dao.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.crazydan.studio.app.healthtracker.model.dao.AppDatabase
import org.crazydan.studio.app.healthtracker.model.dao.HealthPersonDao
import org.crazydan.studio.app.healthtracker.model.dao.HealthRecordDao
import org.crazydan.studio.app.healthtracker.model.dao.HealthRepository
import org.crazydan.studio.app.healthtracker.model.dao.HealthTypeDao
import org.crazydan.studio.app.healthtracker.model.dao.upgrader.MigrationFromV2ToV3
import org.crazydan.studio.app.healthtracker.model.dao.upgrader.MigrationFromV3ToV4
import javax.inject.Singleton

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "health_tracker.db"
        )
            //.fallbackToDestructiveMigration(false)
            .addMigrations(
                MigrationFromV2ToV3(),
                MigrationFromV3ToV4(),
            )
            .build()
    }

    @Provides
    fun provideHealthPersonDao(database: AppDatabase): HealthPersonDao {
        return database.healthPersonDao()
    }

    @Provides
    fun provideHealthTypeDao(database: AppDatabase): HealthTypeDao {
        return database.healthTypeDao()
    }

    @Provides
    fun provideHealthRecordDao(database: AppDatabase): HealthRecordDao {
        return database.healthRecordDao()
    }

    @Provides
    @Singleton
    fun provideHealthRepository(
        healthPersonDao: HealthPersonDao,
        healthTypeDao: HealthTypeDao,
        healthRecordDao: HealthRecordDao,
    ): HealthRepository {
        return HealthRepository(healthPersonDao, healthTypeDao, healthRecordDao)
    }
}