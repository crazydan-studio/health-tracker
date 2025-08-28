// HealthRecord.kt
package org.crazydan.studio.app.healthtracker.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "health_records",
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