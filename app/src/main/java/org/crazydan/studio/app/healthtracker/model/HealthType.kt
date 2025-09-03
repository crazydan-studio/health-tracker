package org.crazydan.studio.app.healthtracker.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.crazydan.studio.app.healthtracker.util.genCode

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

    val limit: HealthLimit,

    /** 测量指标列表 */
    val measures: List<HealthMeasure>,
)

/** 测量指标 */
data class HealthMeasure(
    /** 唯一标识，自动生成，用于避免名字变更造成关联引用失效 */
    val code: String,
    /** 指标名称，如，空腹 8h */
    val name: String,
    val limit: HealthLimit,
)

data class HealthLimit(
    /** 上限值 */
    val upper: Float? = null,
    /** 下限值 */
    val lower: Float? = null,
) {
    override fun toString(): String {
        return "${lower ?: "*"} ~ ${upper ?: "*"}"
    }
}

fun genMeasureCode(): String {
    return genCode(8)
}

fun getMeasureNameByCode(healthType: HealthType, measureCode: String): String {
    return healthType.measures.firstOrNull { it.code == measureCode }?.name ?: "<关联缺失>"
}