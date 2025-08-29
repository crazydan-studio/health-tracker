package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.NormalRange

/**
 * 计算特定 range 的健康记录平均值
 */
private fun calculateRangeAverage(records: List<HealthRecord>, rangeName: String): Double {
    val rangeRecords = records.filter { it.rangeName == rangeName }
    if (rangeRecords.isEmpty()) return 0.0

    return rangeRecords.map { it.value.toDouble() }.average()
}

/**
 * 计算特定 range 的健康记录数量
 */
private fun calculateRangeCount(records: List<HealthRecord>, rangeName: String): Int {
    return records.count { it.rangeName == rangeName }
}

/**
 * 根据平均值与范围的关系确定背景颜色
 */
private fun getRangeColor(range: NormalRange, averageValue: Double): Color {
    return when {
        averageValue > range.upperLimit -> Color(0xFFF44336) // 红色 - 高于上限
        averageValue < range.lowerLimit -> Color(0xFFFFC107) // 黄色 - 低于下限
        else -> Color(0xFF4CAF50) // 绿色 - 在正常范围内
    }
}

/**
 * 圆形平均值显示组件
 */
@Composable
fun HealthRecordAverageCircle(
    label: String,
    range: NormalRange,
    records: List<HealthRecord>,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 8.dp,
) {
    val value = calculateRangeAverage(records, range.name)
    // 根据平均值与范围的关系确定背景颜色
    val backgroundColor = getRangeColor(range, value)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // 绘制背景圆形
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = backgroundColor.copy(alpha = 0.2f),
                radius = size.toPx() / 2
            )

            // 绘制圆形边框
            drawCircle(
                color = backgroundColor,
                style = Stroke(width = strokeWidth.toPx())
            )
        }

        // 显示文本信息
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 显示范围名称
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = strokeWidth)
            )

            Spacer(modifier = Modifier.height(4.dp))
            // 显示平均值
            Text(
                text = "%.1f".format(value),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}