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
    val name: String,           // 类型名称，如"血糖"
    val unit: String,           // 单位，如"mmol/L"
    val ranges: List<NormalRange> // 正常范围列表
)

data class NormalRange(
    val name: String,    // 范围名称，如"空腹8h"
    val lowerLimit: Float, // 下限值
    val upperLimit: Float, // 上限值
)