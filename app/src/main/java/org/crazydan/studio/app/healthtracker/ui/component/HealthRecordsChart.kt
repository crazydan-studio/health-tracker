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
import org.crazydan.studio.android.echarts.compose.ECharts
import org.crazydan.studio.app.healthtracker.model.HealthLimit
import org.crazydan.studio.app.healthtracker.model.HealthMeasure
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
    modifier: Modifier = Modifier,
    healthType: HealthType,
    healthRecords: List<HealthRecord>,
) {
    val chartData = remember(healthRecords) {
        createChartData(healthType, healthRecords)
    }
    val chartOption = remember(healthRecords) {
        createChartOption(healthType, chartData)
    }

    val bgColor = MaterialTheme.colorScheme.background

    ECharts(
        modifier = modifier,
        useDarkTheme = isInDarkTheme(),
        option = chartOption.also {
            it.theme {
                backgroundColor(rgba(bgColor))
            }
        },
    )
}

private fun createChartOption(
    healthType: HealthType,
    chartData: ChartData,
): ECharts.Option {
    val option = ECharts.option {
        tooltip {
            triggerBy { axis }
            axisPointer { type { cross } }
        }
        legend {
            type { plain }
            margin { top(20.px) }
        }

        dataZoom {
            slider {
                margin {
                    //top(90f.pct)
                    left(10f.pct)
                }
                filterMode { filter }
                window {
//                    // 定位到最近一周的数据
//                    val start = max(0, chartData.days.size - 7)
//                    val end = chartData.days.size
//                    range(start.idx, end.idx)
                    range(0f.pct, 100f.pct)
                }
            }
        }
    }

    option.grid {
        showBorder(false)
        margin {
            horizontal(15f.pct)
            //bottom(15f.pct)
        }

        xAxis {
            type {
                category {
                    data {
                        chartData.days.map {
                            item(it) {}
                        }
                    }
                }
            }
            axisTick {
                show(true)
                alignWithLabel(true)
            }
        }
        yAxis {
            name("${healthType.name} (${healthType.unit})") { position { middle() } }

            type { value { fromZero(healthType.limit.lower != null) } }

            healthType.limit.lower?.let { value ->
                markLine {
                    value(value)
                    name("$value ⤓")
                    label {
                        position { end }
                        formatter("{b}")
                    }
                }
            }
            healthType.limit.upper?.let { value ->
                markLine {
                    value(value)
                    name("$value ⤒")
                    label {
                        position { end }
                        formatter("{b}")
                    }
                }
            }
        }
    }

    option.series {
        chartData.lines.forEach { entry ->
            val seriesName = chartData.measures[entry.key]!!
            val seriesLimit = chartData.measureLimits[entry.key]!!

            line {
                name(seriesName)
                smooth(true)
                connectNulls(true)

                data {
                    dimension("x", "y") {
                        x("x")
                        y("y")
                    }

                    entry.value.onEachIndexed { index, value ->
                        item(index, value) {}
                    }
                }

                if (seriesLimit.lower != null && seriesLimit.upper != null) {
                    markArea {
                        byYAxis {
                            value(seriesLimit.lower, seriesLimit.upper)
                            name("${seriesLimit.upper}\n ~\n${seriesLimit.lower}")
                            label {
                                position { right }
                                formatter("{b}")
                            }
                        }
                    }
                } else if (seriesLimit.lower != null || seriesLimit.upper != null) {
                    markLine {
                        seriesLimit.lower?.let { value ->
                            byYAxis {
                                value(value)
                                name("$value ⤓")
                                label {
                                    position { end }
                                    formatter("{b}")
                                }
                            }
                        }
                        seriesLimit.upper?.let { value ->
                            byYAxis {
                                value(value)
                                name("$value ⤒")
                                label {
                                    position { end }
                                    formatter("{b}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    option.series {
        chartData.dots.forEach { entry ->
            val seriesName = chartData.measures[entry.key]!!
            val seriesLimit = chartData.measureLimits[entry.key]!!

            candlestick {
                name(seriesName)

                data {
                    dimension("x", "open", "close", "lowest", "highest") {
                        x("x")
                        y("open", "close", "lowest", "highest")
                        tooltip("open" to "最早", "close" to "最晚", "lowest" to "最低", "highest" to "最高")
                    }

                    entry.value.onEachIndexed { index, value ->
                        item(index, *value.toTypedArray()) {}
                    }
                }

                markPoint {
                    byData {
                        byDimension { max("highest") }
                    }
                    byData {
                        byDimension { min("lowest") }
                    }
                }

                markLine {
                    byData {
                        symbol {
                            shape { circle }
                            size(10)
                        }
                        label { show(false) }

                        start {
                            byDimension { max("highest") }
                        }
                        end {
                            byDimension { min("lowest") }
                        }
                    }

                    if (seriesLimit.lower == null || seriesLimit.upper == null) {
                        seriesLimit.lower?.let { value ->
                            byYAxis {
                                value(value)
                                name("$value ⤓")
                                label {
                                    position { end }
                                    formatter("{b}")
                                }
                            }
                        }
                        seriesLimit.upper?.let { value ->
                            byYAxis {
                                value(value)
                                name("$value ⤒")
                                label {
                                    position { end }
                                    formatter("{b}")
                                }
                            }
                        }
                    }
                }

                if (seriesLimit.lower != null && seriesLimit.upper != null) {
                    markArea {
                        byYAxis {
                            value(seriesLimit.lower, seriesLimit.upper)
                            name("${seriesLimit.upper}\n ~\n${seriesLimit.lower}")
                            label {
                                position { right }
                                formatter("{b}")
                            }
                        }
                    }
                }
            }
        }
    }

    return option
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
    val measures =
        healthType.measures.ifEmpty {
            listOf(
                HealthMeasure(
                    code = "",
                    name = healthType.name,
                    limit = HealthLimit(),
                )
            )
        }

    val measureNameMap = mutableMapOf<String, String>()
    val measureLimitMap = mutableMapOf<String, HealthLimit>()
    measures.forEach { measure ->
        val name = measure.name
        val code = measure.code
        measureNameMap.put(code, name)
        measureLimitMap.put(code, measure.limit)

        val maxAmount = maxDataAmountMap.getOrDefault(code, 0)
        if (maxAmount > 1) {
            // 该指标同一天内含多个数据
            dots.put(code, seriesMap.map { entry ->
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
            lines.put(code, seriesMap.map { entry ->
                entry.value
                    .getOrDefault(code, mutableListOf())
                    .firstOrNull()
            })
        } else {
            // 该指标无数据
            lines.put(code, listOf())
        }
    }

    return ChartData(
        measures = measureNameMap,
        measureLimits = measureLimitMap,
        days = days,
        lines = lines,
        dots = dots,
    )
}

private data class ChartData(
    val measures: Map<String, String>,
    val measureLimits: Map<String, HealthLimit>,
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
