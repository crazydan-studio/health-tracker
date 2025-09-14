package org.crazydan.studio.app.healthtracker.model

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-14
 */
data class HealthRecordFilter(
    val startDate: Long = 0,
    val endDate: Long = 0,
)