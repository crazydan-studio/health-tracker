package org.crazydan.studio.app.healthtracker.model

import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-14
 */
@Serializable
data class HealthRecordFilter(
    /** 测量开始日期 */
    val startDate: Long,
    /** 测量结束日期 */
    val endDate: Long,
) {

    /** 确保指定日期在过滤查询范围内 */
    fun includeDate(date: Long?): HealthRecordFilter =
        if (date == null) this
        else
            copy(
                startDate = min(startDate, date),
                endDate = max(endDate, date),
            )
}