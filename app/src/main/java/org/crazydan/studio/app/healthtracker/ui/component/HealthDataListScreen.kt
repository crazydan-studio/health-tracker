package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-30
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> HealthDataListScreen(
    title: @Composable () -> Unit,
    deletedAmount: Long,
    dataList: List<T>,
    onAddData: (() -> Unit)? = null,
    onViewDeleted: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    content: @Composable (T) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = {
                    onNavigateBack?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
                        }
                    }
                },
                actions = {
                    if (deletedAmount > 0) {
                        TextButton(onClick = onViewDeleted) {
                            Icon(Icons.Default.Recycling, contentDescription = "回收站")
                            Badge {
                                val text = if (deletedAmount > 99) "99+" else deletedAmount.toString()
                                Text(text)
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            onAddData?.let {
                FloatingActionButton(onClick = it) {
                    Icon(Icons.Default.Add, contentDescription = "添加数据")
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
                Text("暂无数据，请点击右下角按钮添加")
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
}