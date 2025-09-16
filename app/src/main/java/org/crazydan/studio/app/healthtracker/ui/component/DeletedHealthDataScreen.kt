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
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
        floatingActionButton = {
            if (dataList.isNotEmpty()) {
                FloatingActionButton(onClick = { showConfirmDialog = true }) {
                    Icon(Icons.Default.DeleteForever, contentDescription = "清空")
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
                Text("回收站为空")
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
            title = { Text("操作确认") },
            text = {
                Text("清空后，数据将被永久删除，无法恢复！若要继续该操作，请点击「确认」按钮，否则，请点击「取消」按钮。")
            },
            confirmButton = {
                Button(
                    modifier = Modifier.alpha(0.5f),
                    onClick = {
                        dispatch(onClearAll)

                        showConfirmDialog = false
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}