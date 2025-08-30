package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-30
 */
@Composable
fun HealthDataCard(
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onView: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = { onView?.invoke() })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TextButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除")
                Text(text = "删除")
            }

            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "编辑")
                Text(text = "编辑")
            }
        }
    }
}