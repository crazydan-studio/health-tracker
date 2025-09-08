package org.crazydan.studio.app.healthtracker.ui.theme

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import org.crazydan.studio.app.healthtracker.R

private val DarkColorScheme = darkColorScheme(
    background = Neutral6,
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    background = Neutral98,
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
)

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Composable
fun HealthTrackerTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

fun getThemeResId(resources: Resources): Int {
    val darkTheme = isInDarkTheme(resources)

    return when {
        darkTheme -> R.style.Theme_HealthTracker_Night
        else -> R.style.Theme_HealthTracker_Light
    }
}

@Composable
fun isInDarkTheme(): Boolean = isSystemInDarkTheme()

fun isInDarkTheme(resources: Resources): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}