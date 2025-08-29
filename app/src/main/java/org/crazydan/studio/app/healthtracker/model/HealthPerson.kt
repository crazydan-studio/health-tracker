package org.crazydan.studio.app.healthtracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.crazydan.studio.app.healthtracker.util.getFullName

const val HEALTH_PERSON_TABLE_NAME = "health_person"

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-28
 */
@Entity(tableName = HEALTH_PERSON_TABLE_NAME)
data class HealthPerson(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 称呼 */
    val label: String?,
    /** 姓 */
    val familyName: String,
    /** 名 */
    val givenName: String,

    /** 出生日期，精确到小时 */
    val birthday: Long,
)

fun getPersonLabel(prefix: String?, person: HealthPerson?): String {
    val label = person?.let { it.label ?: getFullName(it.familyName, it.givenName) }

    return prefix?.let { "$it (${label ?: "未知"})" } ?: ""
}