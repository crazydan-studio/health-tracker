package org.crazydan.studio.app.healthtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.crazydan.studio.app.healthtracker.ui.theme.getThemeResId

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@HiltAndroidApp
class HealthTrackerApplication : Application() {

    override fun onCreate() {
        setTheme(getThemeResId(resources))

        super.onCreate()
    }
}