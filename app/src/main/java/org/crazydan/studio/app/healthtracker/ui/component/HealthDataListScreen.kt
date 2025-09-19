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
import androidx.compose.ui.res.stringResource
import org.crazydan.studio.app.healthtracker.R
import org.crazydan.studio.app.healthtracker.ui.Message
import org.crazydan.studio.app.healthtracker.ui.dispatch

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
    onAddData: (() -> Message)? = null,
    onViewDeleted: () -> Message,
    onNavigateBack: (() -> Message)? = null,
    content: @Composable (T) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = {
                    onNavigateBack?.let {
                        IconButton(onClick = {
                            dispatch(it)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(R.string.btn_back),
                            )
                        }
                    }
                },
                actions = {
                    if (deletedAmount > 0) {
                        TextButton(onClick = {
                            dispatch(onViewDeleted)
                        }) {
                            Icon(
                                Icons.Default.Recycling,
                                contentDescription =
                                    stringResource(R.string.btn_trash),
                            )
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
                FloatingActionButton(onClick = {
                    dispatch(it)
                }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription =
                            stringResource(R.string.btn_add),
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
                Text(stringResource(R.string.msg_no_data_to_add))
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