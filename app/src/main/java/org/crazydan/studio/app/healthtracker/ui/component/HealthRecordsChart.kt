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
import org.crazydan.studio.app.healthtracker.ui.theme.isInDarkTheme
import org.crazydan.studio.app.healthtracker.util.formatEpochMillis
import org.crazydan.studio.app.healthtracker.util.genCode
import kotlin.math.max

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
    val maxDataAmountMap = mutableMapOf<String, Int>()
    val seriesMap = mutableMapOf<String, MutableMap<String, MutableList<ChartData.TimeItem>>>()
    records.asReversed().forEach { record ->
        val measure = record.measure
        val datePattern = "yyyy-MM-dd"
        val datetime = formatEpochMillis(record.timestamp, "$datePattern HH:mm")
        val date = datetime.substring(0, datePattern.length)
        val time = datetime.substring(datePattern.length + 1)

        val list = seriesMap.computeIfAbsent(date) {
            mutableMapOf()
        }.computeIfAbsent(measure) {
            mutableListOf()
        }

        list.add(
            ChartData.TimeItem(
                time = time,
                value = record.value,
            )
        )

        maxDataAmountMap.computeIfAbsent(measure) { 0 }
        maxDataAmountMap.computeIfPresent(measure) { k, v -> max(v, list.size) }
    }

    val days = seriesMap.keys
    val lines = mutableMapOf<String, List<ChartData.TimeItem?>>()
    val points = mutableMapOf<String, List<List<ChartData.TimeItem>>>()
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
            points.put(code, seriesMap.map { entry ->
                entry.value.getOrDefault(code, listOf())
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
        points = points,
    )
}

private data class ChartData(
    val measures: Map<String, String>,
    val measureLimits: Map<String, HealthLimit>,
    val days: Collection<String>,
    val lines: Map<String, List<TimeItem?>>,
    val points: Map<String, List<List<TimeItem>>>,
) {

    data class TimeItem(
        val time: String,
        val value: Float,
    )
}

private fun createChartOption(
    healthType: HealthType,
    chartData: ChartData,
): ECharts.Option {
    val option = createChartBase(
        healthType = healthType,
    )

    configChartGrid(
        option = option,
        healthType = healthType,
        chartData = chartData,
    )

    configChartLineSeries(
        option = option,
        chartData = chartData,
    )

    configChartPointSeries(
        option = option,
        chartData = chartData,
    )

    return option
}

private fun createChartBase(
    healthType: HealthType,
): ECharts.Option {
    val option = ECharts.option {
        tooltip {
            position {
                left()
                top()
            }
            triggerBy { axis }
            axisPointer { type { cross } }

            formatter(
                """
                    function (params) {
                        var title = params[0].name;
                        var series = {};
                        var seriesColors = {};
                        params.forEach(function(param) {
                            var key = param.componentIndex;
                            var list = series[key];
                            if (!list) {
                                list = series[key] = [];
                            }
                            
                            var id = param.seriesId || '';
                            var refId = id.substr(id.indexOf(':') + 1);
                            if (id.indexOf('line-stack-') < 0) {
                                refId = id;
                            }
                            seriesColors[refId] = param.color;
                            
                            list.push({
                                id: id,
                                seriesName: param.seriesName,
                                name: param.value[2] || param.seriesName,
                                value: (function(v) {
                                    return v ? v.toFixed(2) + ' ${healthType.unit}' : '-';
                                })(param.value[1])
                            });
                        });
                        
                        var data = [];
                        Object.keys(series).forEach(function(key) {
                            var s = series[key];
                            var first = s[0];
                            if (first.id.indexOf('line-stack-') >= 0  //
                                || (s.length == 1 && first.value == '-') //
                            ) {
                                return;
                            }
                        
                            var color = seriesColors[first.id];
                            data.push({
                                name: first.seriesName,
                                color: color,
                                data: s.map(function(item) {
                                    item.color = color;
                                    return item;
                                })
                            }); 
                        });
                        //console.log(JSON.stringify(data));
                        var html = createTooltip_v1(title, data);
                        return html;
                    }
            """.trimIndent()
            )
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

    return option
}

private fun configChartGrid(
    option: ECharts.Option,
    healthType: HealthType,
    chartData: ChartData,
) {
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
}

private fun configChartLineSeries(
    option: ECharts.Option,
    chartData: ChartData,
) {
    option.series {
        chartData.lines.forEach { entry ->
            val seriesId = genCode(8)
            val seriesName = chartData.measures[entry.key]!!
            val seriesLimit = chartData.measureLimits[entry.key]!!

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

                    entry.value.onEachIndexed { index, ti ->
                        item(index, ti?.value, ti?.time) {}
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
}

private fun configChartPointSeries(
    option: ECharts.Option,
    chartData: ChartData,
) {
    option.series {
        chartData.points.forEach { pointMapEntry ->
            val seriesId = genCode(8)
            val seriesName = chartData.measures[pointMapEntry.key]!!
            val seriesLimit = chartData.measureLimits[pointMapEntry.key]!!

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

                    pointMapEntry.value.onEachIndexed { index, list ->
                        // 同一天的点，映射到相同的 x 坐标位置
                        list.forEach { ti ->
                            item(index, ti.value, ti.time) {}
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

                    pointMapEntry.value.onEachIndexed { index, list ->
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
                    pointMapEntry.value.onEachIndexed { index, list ->
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
}