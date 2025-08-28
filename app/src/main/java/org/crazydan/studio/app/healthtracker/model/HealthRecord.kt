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
    val typeId: Long,           // 关联的健康类型ID
    val value: Float,           // 记录的值
    val timestamp: Long,        // 记录时间戳
    val notes: String = "",     // 可选备注
    val person: String = "",    // 记录人/目的
    val rangeName: String = ""  // 关联的正常范围名称
)