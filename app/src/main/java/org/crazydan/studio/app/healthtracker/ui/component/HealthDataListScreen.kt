package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-30
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDataListScreen(
    title: @Composable () -> Unit,
    deletedAmount: Long,
    onViewDeleted: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
                        }
                    }
                },
                actions = {
                    TextButton(onClick = onViewDeleted) {
                        Icon(Icons.Default.Recycling, contentDescription = "回收站")
                        Badge {
                            val text = if (deletedAmount > 99) "99+" else deletedAmount.toString()
                            Text(text)
                        }
                    }
                }
            )
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}