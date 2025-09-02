package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-02
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditHealthDataScreen(
    title: @Composable () -> Unit,
    // Note: 需采用函数方式，直接传值会因为变量更新导致视图重新布局，
    // 进而使得填写过程中将其他输入重置的问题
    canSave: () -> Boolean,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    val enabled = canSave()
                    Button(
                        onClick = {
                            if (enabled) {
                                onSave()
                            }
                        },
                        enabled = enabled,
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "保存")
                        Text("保存")
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            //verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}