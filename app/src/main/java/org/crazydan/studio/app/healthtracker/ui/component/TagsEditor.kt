package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsEditor(
    allTags: List<String>,
    selectedTags: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddView by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        if (showAddView) {
            TagAddView(
                tags = allTags,
                onSelected = { tag ->
                    onAdd(tag)
                    showAddView = false
                },
                onConfirm = { tag ->
                    onAdd(tag)
                    showAddView = false
                },
                onClose = { showAddView = false },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            TagsView(
                tags = selectedTags,
                onRemove = onRemove,
                onAdd = { showAddView = true },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagAddView(
    tags: List<String>,
    onConfirm: (String) -> Unit,
    onClose: () -> Unit,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputTag by remember { mutableStateOf("") }

    val onDone = fun() {
        if (inputTag.isNotEmpty()) {
            onConfirm(inputTag)
        }
        onClose()
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                OutlinedTextField(
                    value = inputTag,
                    onValueChange = { inputTag = it.trim() },
                    placeholder = { Text("输入标签...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { onDone() }
                    ),
                    modifier = Modifier.weight(1f),
                )

                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "取消",
                    )
                }

                IconButton(onClick = onDone) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "确认",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (tags.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    FlowRow(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        tags.forEach { tag ->
                            AssistChip(
                                onClick = { onSelected(tag) },
                                label = { Text(tag) },
                                colors = AssistChipDefaults.assistChipColors(
                                    labelColor = MaterialTheme.colorScheme.primary
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagsView(
    tags: List<String>,
    onRemove: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tags.forEach { tag ->
            InputChip(
                selected = true,
                onClick = { onRemove(tag) },
                label = { Text(tag) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "移除标签",
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = InputChipDefaults.inputChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    trailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }

        AssistChip(
            onClick = onAdd,
            label = { Text("添加标签") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加标签",
                    modifier = Modifier.size(16.dp)
                )
            }
        )
    }
}