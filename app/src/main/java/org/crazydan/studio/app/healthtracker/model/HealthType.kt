package org.crazydan.studio.app.healthtracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

const val HEALTH_TYPE_TABLE_NAME = "health_type"

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Entity(tableName = HEALTH_TYPE_TABLE_NAME)
data class HealthType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deleted: Boolean = false,

    /** 关联的 [HealthPerson] id */
    val personId: Long,

    /** 类型名称，如，血糖 */
    val name: String,
    /** 数据单位，如，mmol/L */
    val unit: String,
    /** 正常范围列表 */
    val ranges: List<NormalRange>,
)

/** 正常范围 */
data class NormalRange(
    /** 范围名称，如，空腹 8h */
    val name: String,
    /** 上限值 */
    val upperLimit: Float,
    /** 下限值 */
    val lowerLimit: Float,
)