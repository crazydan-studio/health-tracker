package org.crazydan.studio.app.healthtracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

const val HEALTH_RECORD_TABLE_NAME = "health_record"

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Entity(tableName = HEALTH_RECORD_TABLE_NAME)
data class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deleted: Boolean = false,

    /** 关联的 [HealthType] id */
    val typeId: Long,
    /** 关联的 [HealthPerson] id */
    val personId: Long,

    /** 数据值 */
    val value: Float,
    /** 采集时间，默认与 [createdAt] 相同 */
    val timestamp: Long,

    /** 关联的 [HealthMeasure.code] */
    val measure: String = "",
    /** 标签列表 */
    val tags: List<String>,

    /** 创建时间 */
    val createdAt: Long,
)