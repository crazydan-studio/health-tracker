package org.crazydan.studio.app.healthtracker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.crazydan.studio.app.healthtracker.data.AppDatabase
import org.crazydan.studio.app.healthtracker.data.HealthRecordDao
import org.crazydan.studio.app.healthtracker.data.HealthRepository
import org.crazydan.studio.app.healthtracker.data.HealthTypeDao
import javax.inject.Singleton

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
            .fallbackToDestructiveMigration(false)
            .build()
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
        healthTypeDao: HealthTypeDao,
        healthRecordDao: HealthRecordDao
    ): HealthRepository {
        return HealthRepository(healthTypeDao, healthRecordDao)
    }
}