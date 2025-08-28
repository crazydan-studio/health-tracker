package org.crazydan.studio.app.healthtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HealthTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 可以在这里添加应用初始化代码
    }
}