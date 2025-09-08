package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.crazydan.studio.android.echarts.ECharts
import org.crazydan.studio.android.echarts.EChartsOptions
import org.crazydan.studio.android.echarts.option.AxisData
import org.crazydan.studio.android.echarts.option.AxisType
import org.crazydan.studio.android.echarts.option.DataZoomType
import org.crazydan.studio.android.echarts.option.Series
import org.crazydan.studio.android.echarts.option.SeriesLabel
import org.crazydan.studio.android.echarts.option.SeriesMarkData
import org.crazydan.studio.android.echarts.option.SeriesMarkLine
import org.crazydan.studio.android.echarts.option.SeriesMarkPoint
import org.crazydan.studio.android.echarts.option.Size
import org.crazydan.studio.android.echarts.option.TooltipAxisPointer
import org.crazydan.studio.android.echarts.option.TooltipTrigger
import org.crazydan.studio.android.echarts.option.dataZoom
import org.crazydan.studio.android.echarts.option.grid
import org.crazydan.studio.android.echarts.option.legend
import org.crazydan.studio.android.echarts.option.series
import org.crazydan.studio.android.echarts.option.theme
import org.crazydan.studio.android.echarts.option.tooltip
import org.crazydan.studio.android.echarts.option.xAxis
import org.crazydan.studio.android.echarts.option.yAxis
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.ui.screen.PreviewSample
import org.crazydan.studio.app.healthtracker.ui.theme.isInDarkTheme
import org.crazydan.studio.app.healthtracker.util.formatEpochMillis
import java.sql.Timestamp
import java.util.Calendar
import java.util.Date
import kotlin.math.max

// 时间过滤选项
enum class TimeFilter {
    DAY, WEEK, MONTH, YEAR
}

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Composable
fun HealthRecordsChart(
    healthType: HealthType,
    healthRecords: List<HealthRecord>,
    modifier: Modifier = Modifier
) {
    val chartData = remember(healthRecords) {
        createChartData(healthType, healthRecords)
    }
    val chartOptions = remember(chartData) {
        createChartOptions(chartData)
    }

    ECharts(
        useDarkTheme = isInDarkTheme(),
        options = chartOptions.theme(
            backgroundColor = MaterialTheme.colorScheme.background,
        ),
        modifier = modifier,
    )
}

private fun createChartOptions(
    data: ChartData,
): EChartsOptions {
    val options = EChartsOptions.tooltip(
        trigger = TooltipTrigger.Axis,
        axisPointer = TooltipAxisPointer(
            type = TooltipAxisPointer.Type.Cross,
        ),
    ).legend(
        top = Size.pixel(20),
    ).grid(
        left = Size.percent(10f),
        right = Size.percent(10f),
        bottom = Size.percent(15f),
    ).xAxis(
        type = AxisType.Category,
        data = data.days.map { AxisData(value = it) },
    ).yAxis(
        scale = true,
    ).dataZoom(
        type = DataZoomType.Slider,
        top = Size.percent(90f),
        // 定位到最近一周的数据
        startValueIndex = max(0, data.days.size - 7),
        endValueIndex = data.days.size,
    )

    val series = mutableListOf<Series>()
    data.lines.forEach { entry ->
        series.add(
            Series.Line(
                name = entry.key,
                data = entry.value.map { Series.Line.Data(value = it) },
            )
        )
    }
    data.dots.forEach { entry ->
        series.add(
            Series.Candlestick(
                name = entry.key,
                dimensions = listOf("最早", "最晚", "最低", "最高"),
                data = entry.value.map { Series.Candlestick.Data(value = it) },
                markPoint = SeriesMarkPoint(
                    data = listOf(
                        SeriesMarkData(
                            type = SeriesMarkData.Type.Max,
                            valueIndex = 4,
                        ),
                        SeriesMarkData(
                            type = SeriesMarkData.Type.Min,
                            valueIndex = 3,
                        ),
                    ),
                ),
                markLine = SeriesMarkLine(
                    data = listOf(
                        SeriesMarkLine.Range(
                            from = SeriesMarkData(
                                type = SeriesMarkData.Type.Max,
                                valueIndex = 4,
                                symbol = SeriesMarkData.Symbol.Circle,
                                symbolSize = 10,
                                label = SeriesLabel(show = false),
                            ),
                            to = SeriesMarkData(
                                type = SeriesMarkData.Type.Min,
                                valueIndex = 3,
                                symbol = SeriesMarkData.Symbol.Circle,
                                symbolSize = 10,
                                label = SeriesLabel(show = false),
                            ),
                        ),
                    ),
                ),
            )
        )
    }

    return options.series(series)
}

private fun createChartData(
    healthType: HealthType,
    records: List<HealthRecord>,
): ChartData {
    val maxDataAmountMap = mutableMapOf<String, Int>()
    val seriesMap = mutableMapOf<String, MutableMap<String, MutableList<Float>>>()
    records.asReversed().forEach { record ->
        val measure = record.measure
        val timestamp = formatEpochMillis(record.timestamp, "yyyy-MM-dd")

        val list = seriesMap.computeIfAbsent(timestamp) {
            mutableMapOf()
        }.computeIfAbsent(measure) {
            mutableListOf()
        }
        list.add(record.value)

        maxDataAmountMap.computeIfAbsent(measure) { 0 }
        maxDataAmountMap.computeIfPresent(measure) { k, v -> max(v, list.size) }
    }

    val days = seriesMap.keys
    val lines = mutableMapOf<String, List<Number?>>()
    val dots = mutableMapOf<String, List<List<Number?>>>()

    healthType.measures.forEach { measure ->
        val name = measure.name
        val code = measure.code
        val maxAmount = maxDataAmountMap.getOrDefault(code, 0)

        if (maxAmount > 1) {
            // 该指标同一天内含多个数据
            dots.put(name, seriesMap.map { entry ->
                val list = entry.value.getOrDefault(code, mutableListOf())

                // 对应 K 线图：open，close，lowest，highest
                if (list.isEmpty()) {
                    listOf(null, null, null, null)
                } else {
                    val first = list.first()
                    val last = list.last()
                    val min = list.min()
                    val max = list.max()

                    listOf(first, last, min, max)
                }
            })
        } else if (maxAmount == 1) {
            // 该指标同一天内仅含单个数据
            lines.put(name, seriesMap.map { entry ->
                entry.value
                    .getOrDefault(code, mutableListOf())
                    .firstOrNull()
            })
        } else {
            // 该指标无数据
            lines.put(name, listOf())
        }
    }

    return ChartData(
        days = days,
        lines = lines,
        dots = dots,
    )
}

private data class ChartData(
    val days: Collection<String>,
    val lines: Map<String, List<Number?>>,
    val dots: Map<String, List<List<Number?>>>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFilterSelector(
    selectedFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val filterNames = mapOf(
        TimeFilter.DAY to "日",
        TimeFilter.WEEK to "周",
        TimeFilter.MONTH to "月",
        TimeFilter.YEAR to "年"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = filterNames[selectedFilter] ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("时间范围") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TimeFilter.entries.forEach { filter ->
                DropdownMenuItem(
                    text = { Text(filterNames[filter] ?: "") },
                    onClick = {
                        onFilterSelected(filter)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 根据时间过滤器筛选记录
fun filterRecordsByTime(records: List<HealthRecord>, filter: TimeFilter): List<HealthRecord> {
    if (records.isEmpty()) return emptyList()

    val calendar = Calendar.getInstance()
    val now = Date()

    return when (filter) {
        TimeFilter.DAY -> {
            calendar.time = now
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val startOfDay = calendar.timeInMillis

            records.filter { it.createdAt >= startOfDay }
        }

        TimeFilter.WEEK -> {
            calendar.time = now
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            val startOfWeek = calendar.timeInMillis

            records.filter { it.createdAt >= startOfWeek }
        }

        TimeFilter.MONTH -> {
            calendar.time = now
            calendar.add(Calendar.MONTH, -1)
            val startOfMonth = calendar.timeInMillis

            records.filter { it.createdAt >= startOfMonth }
        }

        TimeFilter.YEAR -> {
            calendar.time = now
            calendar.add(Calendar.YEAR, -1)
            val startOfYear = calendar.timeInMillis

            records.filter { it.createdAt >= startOfYear }
        }
    }
}

@Preview
@Composable
private fun HealthRecordsChartPreview() {
    HealthRecordsChart(
        healthType = PreviewSample().createHealthType(),
        healthRecords = listOf(
            HealthRecord(
                id = 0, typeId = 0, personId = 0,
                value = 10.2f,
                createdAt = Timestamp.valueOf("2025-08-10 08:10:00.000").time,
                timestamp = Timestamp.valueOf("2025-08-10 08:10:00.000").time,
                measure = "kongfu",
                tags = listOf(),
            ),
            HealthRecord(
                id = 0, typeId = 0, personId = 0,
                value = 8.2f,
                createdAt = Timestamp.valueOf("2025-08-10 14:23:00.000").time,
                timestamp = Timestamp.valueOf("2025-08-10 14:23:00.000").time,
                measure = "canhou",
                tags = listOf(),
            ),
            HealthRecord(
                id = 0, typeId = 0, personId = 0,
                value = 15.2f,
                createdAt = Timestamp.valueOf("2025-08-11 08:10:00.000").time,
                timestamp = Timestamp.valueOf("2025-08-11 08:10:00.000").time,
                measure = "kongfu",
                tags = listOf(),
            ),
            HealthRecord(
                id = 0, typeId = 0, personId = 0,
                value = 11.2f,
                createdAt = Timestamp.valueOf("2025-08-11 14:23:00.000").time,
                timestamp = Timestamp.valueOf("2025-08-11 14:23:00.000").time,
                measure = "canhou",
                tags = listOf(),
            ),
            HealthRecord(
                id = 0, typeId = 0, personId = 0,
                value = 11.2f,
                createdAt = Timestamp.valueOf("2025-08-12 08:10:00.000").time,
                timestamp = Timestamp.valueOf("2025-08-12 08:10:00.000").time,
                measure = "kongfu",
                tags = listOf(),
            ),
            HealthRecord(
                id = 0, typeId = 0, personId = 0,
                value = 10.2f,
                createdAt = Timestamp.valueOf("2025-08-12 14:23:00.000").time,
                timestamp = Timestamp.valueOf("2025-08-12 14:23:00.000").time,
                measure = "canhou",
                tags = listOf(),
            ),
            HealthRecord(
                id = 0, typeId = 0, personId = 0,
                value = 14.2f,
                createdAt = Timestamp.valueOf("2025-08-13 08:10:00.000").time,
                timestamp = Timestamp.valueOf("2025-08-13 08:10:00.000").time,
                measure = "kongfu",
                tags = listOf(),
            ),
            HealthRecord(
                id = 0, typeId = 0, personId = 0,
                value = 6.2f,
                createdAt = Timestamp.valueOf("2025-08-13 14:23:00.000").time,
                timestamp = Timestamp.valueOf("2025-08-13 14:23:00.000").time,
                measure = "canhou",
                tags = listOf(),
            ),
            HealthRecord(
                id = 0, typeId = 0, personId = 0,
                value = 8.2f,
                createdAt = Timestamp.valueOf("2025-08-14 08:10:00.000").time,
                timestamp = Timestamp.valueOf("2025-08-14 08:10:00.000").time,
                measure = "kongfu",
                tags = listOf(),
            ),
            HealthRecord(
                id = 0, typeId = 0, personId = 0,
                value = 11.2f,
                createdAt = Timestamp.valueOf("2025-08-14 14:23:00.000").time,
                timestamp = Timestamp.valueOf("2025-08-14 14:23:00.000").time,
                measure = "canhou",
                tags = listOf(),
            ),
        ).sortedBy { it.timestamp }
    )
}
