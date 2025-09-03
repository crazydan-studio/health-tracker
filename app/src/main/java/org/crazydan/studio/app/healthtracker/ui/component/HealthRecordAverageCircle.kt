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
import org.crazydan.studio.app.healthtracker.model.HealthMeasure
import org.crazydan.studio.app.healthtracker.model.HealthRecord

/**
 * 圆形平均值显示组件
 */
@Composable
fun HealthRecordAverageCircle(
    label: String,
    records: List<HealthRecord>,
    modifier: Modifier = Modifier,
    measure: HealthMeasure? = null,
    size: Dp = 120.dp,
    strokeWidth: Dp = 8.dp,
) {
    val value = calculateMeasureAverage(records, measure?.code)
    if (value == null) {
        return
    }

    val measureLevel = getMeasureLevel(measure, value)
    val backgroundColor = getMeasureColor(measureLevel)

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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = strokeWidth)
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "%.1f".format(value),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (measureLevel) {
                    MeasureLevel.UpUpper -> "> 上限"
                    MeasureLevel.LowLower -> "> 下限"
                    else -> "正常"
                },
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                maxLines = 1,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = strokeWidth)
            )
        }
    }
}

/**
 * 计算特定测量指标的健康记录平均值
 */
private fun calculateMeasureAverage(records: List<HealthRecord>, measureCode: String?): Double? {
    val measureRecords = measureCode?.let { m ->
        records.filter { it.measure == m }
    } ?: records

    if (measureRecords.isEmpty()) return null

    return measureRecords.map { it.value.toDouble() }.average()
}

private fun getMeasureLevel(measure: HealthMeasure?, averageValue: Double): MeasureLevel {
    return when {
        measure != null
                && measure.limit.upper != null
                && averageValue > measure.limit.upper
            -> MeasureLevel.UpUpper

        measure != null
                && measure.limit.lower != null
                && averageValue < measure.limit.lower
            -> MeasureLevel.LowLower

        else
            -> MeasureLevel.Normal
    }
}

private fun getMeasureColor(measureLevel: MeasureLevel): Color {
    return when (measureLevel) {
        MeasureLevel.UpUpper
            -> Color(0xFFFFC107) // 黄色 - 高于上限
        MeasureLevel.LowLower
            -> Color(0xFFF44336) // 红色 - 低于下限
        else
            -> Color(0xFF4CAF50) // 绿色 - 在正常范围内
    }
}

enum class MeasureLevel {
    Normal, UpUpper, LowLower
}