package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-30
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> HealthDataListScreen(
    title: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    deletedMessage: (T) -> String,
    onUndelete: (T) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    content: @Composable (PaddingValues, afterDeleted: (T) -> Unit) -> Unit
) {
    val deletedDataChannel = remember { Channel<T>(Channel.UNLIMITED) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deletedDataChannel) {
        deletedDataChannel.receiveAsFlow().collect { data ->
            val result = snackbarHostState.showSnackbar(
                message = deletedMessage(data),
                actionLabel = "撤销",
                duration = SnackbarDuration.Long
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    onUndelete(data)
                }

                SnackbarResult.Dismissed -> {
                }
            }
        }
    }

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
                }
            )
        },
        floatingActionButton = floatingActionButton,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { padding ->
        content(padding) { data ->
            deletedDataChannel.trySend(data)
        }
    }
}