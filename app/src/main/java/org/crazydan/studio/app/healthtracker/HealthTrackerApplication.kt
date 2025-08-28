package org.crazydan.studio.app.healthtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@HiltAndroidApp
class HealthTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 可以在这里添加应用初始化代码
    }
}