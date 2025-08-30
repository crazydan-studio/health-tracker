package org.crazydan.studio.app.healthtracker.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

const val HEALTH_RECORD_TABLE_NAME = "health_record"

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Entity(
    tableName = HEALTH_RECORD_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = HealthType::class,
            parentColumns = ["id"],
            childColumns = ["typeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
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
    /** 数据时间戳，默认与 [createdAt] 相同 */
    val timestamp: Long,

    /** 关联的 [NormalRange] 名称 */
    val rangeName: String = "",
    /** 可选备注 */
    val notes: String = "",

    /** 创建时间 */
    val createdAt: Long,
)