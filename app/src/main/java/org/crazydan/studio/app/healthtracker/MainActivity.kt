package org.crazydan.studio.app.healthtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import org.crazydan.studio.app.healthtracker.ui.HealthTrackerApp
import org.crazydan.studio.app.healthtracker.ui.theme.HealthTrackerTheme
import org.crazydan.studio.app.healthtracker.ui.theme.getThemeResId

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getThemeResId(resources))

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HealthTrackerApp()
                }
            }
        }
    }
}