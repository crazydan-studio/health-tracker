package org.crazydan.studio.app.healthtracker.model

import kotlinx.serialization.Serializable

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-14
 */
@Serializable
data class HealthRecordFilter(
    val startDate: Long,
    val endDate: Long,
)