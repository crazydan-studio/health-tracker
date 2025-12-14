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
import org.crazydan.studio.app.healthtracker.util.Pattern_yyyy_MM_dd_HH_mm
import org.crazydan.studio.app.healthtracker.util.formatEpochMillis
import org.crazydan.studio.app.healthtracker.util.genCode
import kotlin.math.floor

private const val Dimension_x = "x"
private const val Dimension_y = "y"
private const val Dimension_x_label = "x-label"
private const val Dimension_tags = "tags"

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
    val dateRanges = mutableMapOf<String, MutableSet<String>>()
    // <measure code, [item, ...]>
    val seriesMap = mutableMapOf<String, MutableList<TimeItem>>()
    // Note: records 是按 timestamp 降序排序的，这里需将其调整为升序
    records.asReversed().forEach { record ->
        val measure = record.measure
        val datetime = formatEpochMillis(record.timestamp, Pattern_yyyy_MM_dd_HH_mm)
        val date = datetime.substringBefore(" ")

        val datetimes = dateRanges.computeIfAbsent(date) { mutableSetOf() }
        datetimes.add(datetime)

        seriesMap.computeIfAbsent(measure) {
            mutableListOf()
        }.add(
            TimeItem(
                value = record.value,
                tags = record.tags,
                date = date,
                datetime = datetime,
                indexInDay = datetimes.size - 1,
                indexInDateRanges = dateRanges.size - 1,
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
        dateRanges = dateRanges,
        lines = seriesMap,
    )
}

private data class ChartData(
    // <measure code, measure name>
    val measures: Map<String, String>,
    // <measure code, limit>
    val measureLimits: Map<String, HealthLimit>,
    // <'2025-01-02', ['2025-01-02 08:20', ...]>
    val dateRanges: Map<String, Set<String>>,
    // <measure code, [item, ...]>
    val lines: Map<String, List<TimeItem>>,
) {

    data class TimeItem(
        val value: Float,
        val tags: List<String>,
        /** 日期 */
        val date: String,
        /** 时间 */
        val datetime: String,
        /**
         * 当前记录在同一天 [date] 中的数据列表中所处的序号：
         * 相同 [datetime] 的记录有相同的序号
         */
        val indexInDay: Int,
        /** 当前记录的 [date] 在 dateRanges 中的序号 */
        val indexInDateRanges: Int,
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
            axisPointer {
                type { cross }
                label {
                    formatter(
                        // console.log(JSON.stringify(params));
                        // return echarts.format.formatTime('yyyy-MM-dd hh:mm', params.value);
                        """
                        function (params) {
                            if (params.axisDimension == '${Dimension_y}') {
                                return params.value.toFixed(2);
                            } else if (params.axisDimension == '${Dimension_x}') {
                                var index = params.seriesData[0].dimensionNames.indexOf('${Dimension_x_label}');
                                return params.seriesData[0].value[index];
                            }
                            return '';
                        }
                    """.trimIndent()
                    )
                }
            }

            formatter(
                """
                    function (params) {
                        var title = params[0].axisValueLabel || params[0].name;
                        var data = params.map(function (param) {
                            return {
                                name: param.seriesName,
                                color: param.color,
                                value: (function (v) {
                                    return v ? v.toFixed(2) + ' ${healthType.unit}' : '-';
                                })(param.value[param.encode.y[0]]),
                                tags: (param.value[param.dimensionNames.indexOf('${Dimension_tags}')] || '')
                                        .split(',')
                                        .filter(function (v) {
                                            return !!v;
                                        })
                            };
                        });
                
                        return createTooltip_v1(title, data);
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
    val dateRanges = chartData.dateRanges.keys
    val dateRangeSize = dateRanges.size

    option.grid {
        showBorder(false)
        margin {
            horizontal(15f.pct)
            //bottom(15f.pct)
        }

        xAxis {
            type { value {} }
            minValue(0f)
            maxValue(dateRangeSize.toFloat())

            label { show(false) }
            splitLine { show(false) }
            tick {
                // 在一天的中心位置显示刻度：最多只显示 7~14 个
                val factor = dateRangeSize / 7f
                values(
                    dateRanges.mapIndexed { index, _ ->
                        if (factor > 2f) {
                            floor((index * factor).toDouble()).toInt()
                        } else {
                            index
                        }
                    }
                        .filter { index -> index <= dateRangeSize }
                        .map { it + 0.5f }
                )
            }

            // 标记天的范围
            for (index in 0 until dateRangeSize) {
                val colors = listOf(
                    rgba(115, 192, 222, 0.03f),
                    rgba(0, 0, 0, 0f),
                )
                val c = colors[index % colors.size]

                markArea {
                    value(index, index + 1)
                    style {
                        color(c)
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
                dimension(Dimension_x, Dimension_y, Dimension_x_label, Dimension_tags) {
                    x(Dimension_x)
                    y(Dimension_y)
                }

                measureData.forEach {
                    // 将一天内的数据按比例均分（左右预留两个空位），确保每天的数据所占用的横轴宽度始终一致
                    val total = chartData.dateRanges.getOrDefault(it.date, setOf()).size + 2
                    val index = it.indexInDateRanges + ((it.indexInDay + 1f) / total)

                    item(index, it.value, it.datetime, it.tags.joinToString(",")) {}
                }
            }

            markPoint {
                byData {
                    byDimension { max(Dimension_y) }
                }
                byData {
                    symbol { rotate(180) }
                    label { position { insideBottom } }
                    byDimension { min(Dimension_y) }
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