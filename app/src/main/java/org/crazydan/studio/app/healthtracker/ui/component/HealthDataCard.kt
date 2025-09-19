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
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.R
import org.crazydan.studio.app.healthtracker.ui.Message
import org.crazydan.studio.app.healthtracker.ui.dispatch

class HealthDataCardActions(
    val onEdit: (() -> Message)? = null,
    val onView: (() -> Message)? = null,
    val onDelete: (() -> Message)? = null,
    val onUndelete: (() -> Message)? = null,
)

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-30
 */
@Composable
fun HealthDataCard(
    actions: HealthDataCardActions,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = {
                actions.onView?.let {
                    dispatch(it)
                }
            })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            content = content
        )

        if (actions.onEdit != null || actions.onDelete != null || actions.onUndelete != null) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                actions.onDelete?.let { action ->
                    TextButton(
                        modifier = Modifier.alpha(0.4f),
                        onClick = {
                            dispatch(action)
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription =
                                stringResource(R.string.btn_delete),
                        )
                        Text(text = stringResource(R.string.btn_delete))
                    }
                }
                actions.onUndelete?.let { action ->
                    TextButton(onClick = {
                        dispatch(action)
                    }) {
                        Icon(
                            Icons.Default.Restore,
                            contentDescription =
                                stringResource(R.string.btn_undelete),
                        )
                        Text(
                            text = stringResource(R.string.btn_undelete)
                        )
                    }
                }

                actions.onEdit?.let { action ->
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        dispatch(action)
                    }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription =
                                stringResource(R.string.btn_edit),
                        )
                        Text(text = stringResource(R.string.btn_edit))
                    }
                }
            }
        }
    }
}