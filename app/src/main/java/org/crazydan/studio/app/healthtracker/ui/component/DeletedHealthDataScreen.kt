package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import org.crazydan.studio.app.healthtracker.R
import org.crazydan.studio.app.healthtracker.ui.Message
import org.crazydan.studio.app.healthtracker.ui.dispatch

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-31
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DeletedHealthDataScreen(
    title: @Composable () -> Unit,
    dataList: List<T>,
    onClearAll: (() -> Message),
    onNavigateBack: (() -> Message),
    content: @Composable (T) -> Unit,
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = {
                        dispatch(onNavigateBack)
                    }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription =
                                stringResource(R.string.btn_back),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (dataList.isNotEmpty()) {
                FloatingActionButton(onClick = { showConfirmDialog = true }) {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription =
                            stringResource(R.string.btn_clear),
                    )
                }
            }
        },
    ) { padding ->
        if (dataList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.msg_empty_trash))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(dataList) { data ->
                    content(data)
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(stringResource(R.string.title_confirm_operation)) },
            text = {
                Text(stringResource(R.string.msg_clear_trash_warning))
            },
            confirmButton = {
                Button(
                    modifier = Modifier.alpha(0.5f),
                    onClick = {
                        dispatch(onClearAll)

                        showConfirmDialog = false
                    }
                ) {
                    Text(stringResource(R.string.btn_confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}