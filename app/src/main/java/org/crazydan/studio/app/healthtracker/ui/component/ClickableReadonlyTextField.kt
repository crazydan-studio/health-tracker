package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-19
 */
@Composable
fun ClickableReadonlyTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { /* 禁止直接编辑 */ },
            label = label,
            trailingIcon = trailingIcon,
            readOnly = true,
            // Note: enable 为 true 时，Modifier.clickable 的配置无效，
            // 而若 enable 为 false，则无法保证其聚焦样式与其他输入组件一致，
            // 故而，在其之上覆盖透明层用于接收点击事件，并同时触发对该输入框的聚焦
            enabled = true,
            modifier = modifier
                .focusRequester(focusRequester)
                .focusable(interactionSource = interactionSource),
            interactionSource = interactionSource,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    alpha = 0.1f
                }
                .clickable {
                    focusRequester.requestFocus()

                    onClick()
                }
        )
    }
}