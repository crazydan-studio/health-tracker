package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.crazydan.studio.app.healthtracker.R

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-01
 */
@Composable
fun HealthDataLoadingScreen(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.msg_data_loading)
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeartBeatAnimation(
                size = 120.dp,
                //color = MaterialTheme.colorScheme.primary,
                color = Color(0xd3, 0x2f, 0x2f),
                animationDuration = 1000
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                //color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
private fun HeartBeatAnimation(
    size: Dp = 120.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    animationDuration: Int = 800
) {
    // 创建动画值，控制心形的缩放
    val scale = remember { Animatable(1f) }

    // 启动动画
    LaunchedEffect(Unit) {
        // 无限重复的动画
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = animationDuration
                    1.0f at 0 // 初始大小
                    1.2f at animationDuration / 3 // 放大到最大
                    1.0f at animationDuration * 2 / 3 // 缩小到原始大小
                    1.0f at animationDuration // 保持原始大小
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // 绘制心形
        HeartShape(
            modifier = Modifier.size(size),
            color = color,
            scale = scale.value
        )
    }
}

@Composable
private fun HeartShape(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    scale: Float = 1f
) {
    Box(
        modifier = modifier
            .drawWithCache {
                // 创建心形路径
                val path = createHeartPath(size)

                onDrawWithContent {
                    // 应用缩放变换
                    withTransform({
                        scale(scale, scale, pivot = center)
                        rotate(45f, pivot = Offset(0f, size.height * 0.7f))
                    }) {
                        // 绘制心形
                        drawPath(
                            path = path,
                            color = color,
                            style = Fill
                        )
                    }
                }
            }
    )
}

// 创建心形路径
private fun createHeartPath(size: Size): Path {
    val width = size.width
    val height = size.height

    return Path().apply {
//        // 心形参数方程:
//        // x = 16sin³(t)
//        // y = 13cos(t) - 5cos(2t) - 2cos(3t) - cos(4t)
//
//        val centerX = width / 2
//        val centerY = height / 2
//        val scale = minOf(width, height) / 32f // 缩放因子
//
//        // 移动到起始点
//        val startX = 16 * scale * sin(0f).pow(3)
//        val startY = -(13 * scale * cos(0f) - 5 * scale * cos(2 * 0f) - 2 * scale * cos(3 * 0f) - scale * cos(4 * 0f))
//        moveTo(centerX + startX, centerY + startY)
//
//        // 绘制心形曲线
//        for (t in 0..100) {
//            val angle = t * 2 * PI.toFloat() / 100
//            val x = 16 * scale * sin(angle).pow(3)
//            val y =
//                -(13 * scale * cos(angle) - 5 * scale * cos(2 * angle) - 2 * scale * cos(3 * angle) - scale * cos(4 * angle))
//
//            lineTo(centerX + x, centerY + y)
//        }

//        moveTo(width / 2, height * 0.3f) // Top point
//        cubicTo(
//            width * 0.9f, height * 0.0f,
//            width * 1.0f, height * 0.6f,
//            width / 2, height * 0.85f
//        ) // Right curve
//        cubicTo(
//            width * 0.0f, height * 0.6f,
//            width * 0.1f, height * 0.0f,
//            width / 2, height * 0.3f
//        ) // Left curve

        // https://stackoverflow.com/a/33522944
        val length = width / 2
        val x = width / 2
        val y = height / 2

        moveTo(x, y)
        lineTo(x - length, y)
        arcTo(
            Rect(x - length - (length / 2), y - length, x - (length / 2), y),
            90f,
            180f, false
        )
        arcTo(
            Rect(x - length, y - length - (length / 2), x, y - (length / 2)),
            180f, 180f, false
        )
        lineTo(x, y)

        close()
    }
}

@Preview
@Composable
fun HealthDataLoadingScreenPreview() {
    HealthDataLoadingScreen()
}
