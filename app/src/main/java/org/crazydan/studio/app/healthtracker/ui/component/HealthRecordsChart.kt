package org.crazydan.studio.app.healthtracker.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.crazydan.studio.android.echarts.ECharts
import org.crazydan.studio.android.echarts.compose.ECharts
import org.crazydan.studio.app.healthtracker.model.HealthLimit
import org.crazydan.studio.app.healthtracker.model.HealthMeasure
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import org.crazydan.studio.app.healthtracker.ui.component.ChartData.TimeItem
import org.crazydan.studio.app.healthtracker.ui.theme.isInDarkTheme
import org.crazydan.studio.app.healthtracker.util.formatEpochMillis
import org.crazydan.studio.app.healthtracker.util.genCode

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

private fun createChartData(
    healthType: HealthType,
    records: List<HealthRecord>,
): ChartData {
    // <measure code, [item, ...]>
    val seriesMap = mutableMapOf<String, MutableList<TimeItem>>()
    // Note: records 是按 timestamp 降序排序的，这里需将其调整为升序
    records.asReversed().forEach { record ->
        val measure = record.measure

        seriesMap.computeIfAbsent(measure) {
            mutableListOf()
        }.add(
            TimeItem(
                datetime = record.timestamp,
                value = record.value,
            )
        )
    }

    val measures =
        healthType.measures.ifEmpty {
            // Note: 对于无测量指标的数据，采用匿名指标做数据映射
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
    }

    return ChartData(
        measures = measureNameMap,
        measureLimits = measureLimitMap,
        lines = seriesMap,
    )
}

private data class ChartData(
    // <measure code, measure name>
    val measures: Map<String, String>,
    // <measure code, limit>
    val measureLimits: Map<String, HealthLimit>,
    // <measure code, [item, ...]>
    val lines: Map<String, List<TimeItem>>,
) {

    data class TimeItem(
        val datetime: Long,
        val value: Float,
    )
}

private fun createChartOption(
    healthType: HealthType,
    chartData: ChartData,
): ECharts.Option {
    val option = createChartBase(
        healthType = healthType,
        chartData = chartData,
    )

    configChartGrid(
        option = option,
        healthType = healthType,
        chartData = chartData,
    )

    // Note: 需确保图例的顺序保持不变
    chartData.measures.entries.sortedBy { it.value }.forEach { entry ->
        val measureCode = entry.key

        chartData.lines[measureCode]?.let {
            configChartLineSeries(
                option = option,
                measureCode = measureCode,
                measureData = it,
                chartData = chartData,
            )
        }
    }

    return option
}

private fun createChartBase(
    healthType: HealthType,
    chartData: ChartData,
): ECharts.Option {
    val option = ECharts.option {
        tooltip {
            position {
                left()
                top()
            }
            triggerBy { axis }
            axisPointer { type { cross } }
        }

        legend {
            type { plain }
            margin { top(20.px) }
        }

        dataZoom {
            slider {
                // Note: 数据数量小于 2 时，缩放区域的位置会发生漂移，对此，直接不显示
                show(chartData.lines.map { it.value.size }.max() > 1)

                margin {
                    //top(90f.pct)
                    left(10f.pct)
                }
                filterMode { filter }
                window {
                    range(0f.pct, 100f.pct)
                }
            }
        }
    }

    return option
}

private fun configChartGrid(
    option: ECharts.Option,
    healthType: HealthType,
    chartData: ChartData,
) {
    val dateRanges = mutableMapOf<String, MutableList<Long>>()
    chartData.lines.map { it.value }.fold(mutableListOf<Long>()) { acc, items ->
        acc.addAll(items.map { it.datetime })
        acc
    }.also {
        it.sort()
    }.forEach { datetime ->
        val date = formatEpochMillis(datetime, "yyyy-MM-dd")

        dateRanges.computeIfAbsent(date) {
            mutableListOf()
        }.add(datetime)
    }

    option.grid {
        showBorder(false)
        margin {
            horizontal(15f.pct)
            //bottom(15f.pct)
        }

        xAxis {
            type { time() }
            label {
                rotate(-30f)
            }

            // 标记天的范围
            dateRanges.entries.forEachIndexed { index, entry ->
                val ranges = entry.value

                if (ranges.size == 1) {
                    markLine {
                        label { show(false) }
                        style {
                            type { solid }
                            width(8)

                            val colors = listOf(
                                rgba(115, 192, 222, 0.03f),
                                rgba(0, 0, 0, 0f),
                            )
                            color(colors[index % colors.size])
                        }

                        value(ranges.first())
                    }
                } else {
                    markArea {
                        style {
                            val colors = listOf(
                                rgba(115, 192, 222, 0.03f),
                                rgba(0, 0, 0, 0f),
                            )
                            color(colors[index % colors.size])
                        }

                        value(ranges.first(), ranges.last())
                    }
                }
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
}

private fun configChartLineSeries(
    option: ECharts.Option,
    measureCode: String,
    measureData: List<TimeItem>,
    chartData: ChartData,
) {
    option.series {
        val seriesId = genCode(8)
        val seriesName = chartData.measures[measureCode]!!
        val seriesLimit = chartData.measureLimits[measureCode]!!

        line {
            id(seriesId)
            name(seriesName)
            smooth(true)
            connectNulls(true)

            data {
                dimension("x", "y") {
                    x("x")
                    y("y")
                }

                measureData.forEach {
                    item(it.datetime, it.value) {}
                }
            }

            markPoint {
                byData {
                    byDimension { max("y") }
                }
                byData {
                    symbol { rotate(180) }
                    label { position { insideBottom } }
                    byDimension { min("y") }
                }
            }

            if (seriesLimit.lower != null && seriesLimit.upper != null) {
                markArea {
                    style { opacity(0.3f) }

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

private fun configChartPointSeries(
    option: ECharts.Option,
    measureCode: String,
    measureData: List<List<TimeItem>>,
    chartData: ChartData,
) {
    option.series {
        val seriesId = genCode(8)
        val seriesName = chartData.measures[measureCode]!!
        val seriesLimit = chartData.measureLimits[measureCode]!!

        scatter {
            id(seriesId)
            name(seriesName)
            colorBy { data }
            symbol { size(4) }

            data {
                dimension("x", "y") {
                    x("x")
                    y("y")
                }

                measureData.onEachIndexed { index, list ->
                    // 同一天的点，映射到相同的 x 坐标位置
                    list.forEach { ti ->
                        item(index, ti.value) {}
                    }
                }
            }

            markPoint {
                byData {
                    byDimension { max("y") }
                }
                byData {
                    symbol { rotate(180) }
                    label { position { insideBottom } }
                    byDimension { min("y") }
                }
            }

            markLine {
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
                    style { opacity(0.1f) }

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

        val stackCode = "stack:$seriesId"
        line {
            id("line-stack-min:$seriesId")
            name(seriesName)
            smooth(true)
            connectNulls(true)

            symbol { shape { none } }
            stack { name(stackCode) }
            lineStyle { opacity(0.6f) }

            data {
                dimension("x", "y") {
                    x("x")
                    y("y")
                }

                measureData.onEachIndexed { index, list ->
                    val min = list.minOfOrNull { it.value }
                    item(index, min) {}
                }
            }
        }
        line {
            id("line-stack-max:$seriesId")
            name(seriesName)
            smooth(true)
            connectNulls(true)

            symbol { shape { none } }
            stack { name(stackCode) }
            lineStyle { opacity(0.6f) }
            areaStyle { opacity(0.6f) }

            data {
                dimension("x", "y") {
                    x("x")
                    y("y")
                }

                // Note: 这里为前面同名 stack 之间的差值
                measureData.onEachIndexed { index, list ->
                    val min = list.minOfOrNull { it.value }
                    val max = list.maxOfOrNull { it.value }

                    if (min != null && max != null) {
                        item(index, max - min) {}
                    }
                }
            }
        }
    }
}