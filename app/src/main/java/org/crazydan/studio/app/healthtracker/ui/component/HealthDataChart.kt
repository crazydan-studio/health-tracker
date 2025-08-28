package org.crazydan.studio.app.healthtracker.ui.component

import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AADataElement
import com.github.aachartmodel.aainfographics.aachartcreator.AAOptions
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aachartcreator.aa_toAAOptions
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALabel
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAPlotBandsElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AASeriesEvents
import org.crazydan.studio.app.healthtracker.model.HealthRecord
import org.crazydan.studio.app.healthtracker.model.HealthType
import java.sql.Timestamp
import java.util.Calendar
import java.util.Date

// 时间过滤选项
enum class TimeFilter {
    DAY, WEEK, MONTH, YEAR
}

@Composable
fun HealthDataChart(
    healthType: HealthType,
    records: List<HealthRecord>,
    modifier: Modifier = Modifier
) {
    val recordsSample = listOf(
        HealthRecord(
            id = 0, typeId = 0,
            value = 10.2f,
            timestamp = Timestamp.valueOf("2025-08-10 08:10:00.000").time,
            rangeName = "空腹",
        ),
        HealthRecord(
            id = 0, typeId = 0,
            value = 8.2f,
            timestamp = Timestamp.valueOf("2025-08-10 14:23:00.000").time,
            rangeName = "餐后 2h",
        ),
        HealthRecord(
            id = 0, typeId = 0,
            value = 15.2f,
            timestamp = Timestamp.valueOf("2025-08-11 08:10:00.000").time,
            rangeName = "空腹",
        ),
        HealthRecord(
            id = 0, typeId = 0,
            value = 11.2f,
            timestamp = Timestamp.valueOf("2025-08-11 14:23:00.000").time,
            rangeName = "餐后 2h",
        ),
        HealthRecord(
            id = 0, typeId = 0,
            value = 11.2f,
            timestamp = Timestamp.valueOf("2025-08-12 08:10:00.000").time,
            rangeName = "空腹",
        ),
        HealthRecord(
            id = 0, typeId = 0,
            value = 10.2f,
            timestamp = Timestamp.valueOf("2025-08-12 14:23:00.000").time,
            rangeName = "餐后 2h",
        ),
        HealthRecord(
            id = 0, typeId = 0,
            value = 14.2f,
            timestamp = Timestamp.valueOf("2025-08-13 08:10:00.000").time,
            rangeName = "空腹",
        ),
        HealthRecord(
            id = 0, typeId = 0,
            value = 6.2f,
            timestamp = Timestamp.valueOf("2025-08-13 14:23:00.000").time,
            rangeName = "餐后 2h",
        ),
        HealthRecord(
            id = 0, typeId = 0,
            value = 8.2f,
            timestamp = Timestamp.valueOf("2025-08-14 08:10:00.000").time,
            rangeName = "空腹",
        ),
        HealthRecord(
            id = 0, typeId = 0,
            value = 11.2f,
            timestamp = Timestamp.valueOf("2025-08-14 14:23:00.000").time,
            rangeName = "餐后 2h",
        ),
    )

//    var selectedFilter by remember { mutableStateOf(TimeFilter.YEAR) }
    val filteredRecords = recordsSample // filterRecordsByTime(recordsSample, selectedFilter).sortedBy { it.timestamp }

    Column(modifier = modifier) {
//        // 时间过滤器
//        TimeFilterSelector(
//            selectedFilter = selectedFilter,
//            onFilterSelected = { selectedFilter = it }
//        )

        // 图表
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(8.dp)
        ) {
            if (filteredRecords.isEmpty()) {
                Text(
                    text = "暂无数据",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LineChart(
                    healthType = healthType,
                    records = filteredRecords,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun LineChart(
    healthType: HealthType,
    records: List<HealthRecord>,
    modifier: Modifier = Modifier
) {
    val chartModelOptions = remember(records) {
        createSplineChartModelOptions(healthType, records)
    }

    AndroidView(
        factory = { ctx ->
            AAChartView(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )

                aa_drawChartWithChartOptions(chartModelOptions)
            }
        },
        update = { view ->
            view.aa_refreshChartWithChartOptions(
                chartModelOptions
            )
            view.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                createSeries(healthType, records).toTypedArray(), true
            )
        },
        modifier = modifier
    )
}

private fun createSplineChartModelOptions(
    healthType: HealthType,
    records: List<HealthRecord>,
): AAOptions {
    val series: Array<Any> = createSeries(healthType, records).toTypedArray()

    // 创建并配置图表模型
    val options = AAChartModel()
        .chartType(AAChartType.Spline)
        //.backgroundColor("#4b2b7f")
        .dataLabelsEnabled(true)
        .markerRadius(4)
        //.zoomType(AAChartZoomType.X)
        .touchEventEnabled(true)
        .yAxisTitle("${healthType.name} (${healthType.unit})")
        .yAxisMin(0)
        .series(series)
        .aa_toAAOptions()

    val plotColors = arrayOf(
        "rgba(4, 214, 159, 0.2)",
        "rgba(30, 143, 255, 0.2)",
        "rgba(239, 71, 110, 0.2)",
        "rgba(255, 209, 102, 0.2)",
    )
    val plots = healthType.ranges.mapIndexed { index, range ->
        val color = plotColors[index]

        // https://api.highcharts.com/highcharts/xAxis.plotBands.label
        AAPlotBandsElement().label(
            AALabel().text("${range.name} (${range.lowerLimit}~${range.upperLimit} ${healthType.unit})")
        )
            .color(color)
            .from(range.lowerLimit)
            .to(range.upperLimit)
    }
    options.yAxis
        ?.plotBands(plots.toTypedArray())

    val dateFormatter = """
        function(date) {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            
            return month + '-' + day + ' ' + hours + ':' + minutes;
        }
    """.trimIndent()
    options.xAxis
        ?.labels(
            AALabels()
                .useHTML(true)
                // Note: 只能定义唯一一个匿名函数
                .formatter(
                    """
            function() {
                const formatDateTime = $dateFormatter;
                const date = new Date(this.value);
                return formatDateTime(date);
            }
        """.trimIndent()
                )
        )
    options.tooltip
        ?.shared(false)
        ?.formatter(
            """
                    function () {
                        const formatDateTime = $dateFormatter;
                        const date = new Date(this.x);
                        const value = this.y;
                        return formatDateTime(date) + '<br/>'
                                + this.series.name
                                + ': <b>' + value + '${healthType.unit}</b>';
                    }
                """.trimIndent()
        )

    options.plotOptions?.series?.events = AASeriesEvents()
        .legendItemClick(
            """
        function(event) {
            function getVisibleMode(series, name) {
                var allVisible = true;
                var allHidden = true;
                series.forEach(function(s) {
                    if (s.name != name) {
                        allVisible &= s.visible;
                        allHidden &= (!s.visible);
                    }
                });
                if (allVisible && !allHidden)
                    return 'all-visible';
                if (allHidden && !allVisible)
                    return 'all-hidden';
                return 'other-cases';
            }
            function getPlot(plots, name) {
                return plots.filter(function(p) {
                    return p.label.textStr.indexOf(name) >= 0;
                })[0];
            }
            function showPlot(p, shown) {
                if (shown) {
                    p.svgElem.element.style.display = 'unset';
                    p.label.element.style.display = 'unset';
                } else {
                    p.svgElem.element.style.display = 'none';
                    p.label.element.style.display = 'none';
                }
            }
            
            var series = this.chart.series;
            var mode = getVisibleMode(series, this.name);
            
            var plots = this.chart.yAxis[0].plotLinesAndBands;
            var plot = getPlot(plots, this.name);
            plots.forEach(function(p) { showPlot(p, true); });
            
            var enableDefault = false;
            if (!this.visible) {
                enableDefault = true;
            }
            else if (mode == 'all-visible') {
                series.forEach(function(s) { s.hide(); });
                plots.forEach(function(p) { showPlot(p, false); });
                
                this.show();
                showPlot(plot, true);
            }
            else if (mode == 'all-hidden') {
                series.forEach(function(s) { s.show(); });
            }
            else {
                enableDefault = true;
            }
            return enableDefault;
        }
    """.trimIndent()
        )

    return options
}

private fun createSeries(
    healthType: HealthType,
    records: List<HealthRecord>
): List<AASeriesElement> {
    return healthType.ranges.map { range ->
        val data: Array<Any> = records.map { record ->
            if (record.rangeName == range.name) {
                AADataElement().x(record.timestamp).y(record.value)
            } else {
                "null"
            }
        }.filter { it != "null" }.toTypedArray()

        AASeriesElement()
            .name(range.name)
            //.connectNulls(true)
            .data(data)
    }
}

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
            modifier = Modifier.menuAnchor()
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

            records.filter { it.timestamp >= startOfDay }
        }

        TimeFilter.WEEK -> {
            calendar.time = now
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            val startOfWeek = calendar.timeInMillis

            records.filter { it.timestamp >= startOfWeek }
        }

        TimeFilter.MONTH -> {
            calendar.time = now
            calendar.add(Calendar.MONTH, -1)
            val startOfMonth = calendar.timeInMillis

            records.filter { it.timestamp >= startOfMonth }
        }

        TimeFilter.YEAR -> {
            calendar.time = now
            calendar.add(Calendar.YEAR, -1)
            val startOfYear = calendar.timeInMillis

            records.filter { it.timestamp >= startOfYear }
        }
    }
}
