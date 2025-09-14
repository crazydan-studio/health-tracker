package org.crazydan.studio.app.healthtracker.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-08-29
 */
fun calculateAge(birthday: Long): String {
    val birthDate = Instant.ofEpochMilli(birthday).atZone(ZoneId.systemDefault()).toLocalDate()
    val currentDate = LocalDate.now()
    val period = Period.between(birthDate, currentDate)

    return listOf(
        if (period.years > 0) "${period.years} 岁" else null,
        if (period.months > 0) "${period.months} 个月" else null,
        if (period.days > 0) "${period.days} 天" else null,
    )
        .filter { it != null }
        .joinToString(separator = " ")
}

fun getFullName(familyName: String, givenName: String): String {
    val nameSeparator = if (givenName[0].code in 0..255) " " else ""

    return "${familyName}${nameSeparator}${givenName}"
}

/** @param untilToDayEnd 在 [time] 未指定时，是否将 [time] 设定为 23:59:59.999 */
fun toEpochMillis(date: LocalDate, time: LocalTime? = null, untilToDayEnd: Boolean = false): Long {
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime.of(
        date,
        time ?: (
                if (untilToDayEnd) LocalTime.of(23, 59, 59, 9999)
                else LocalTime.of(0, 0, 0, 0)),
        zoneId,
    )

    return zonedDateTime.toInstant().toEpochMilli()
}

fun epochMillisToLocalDate(millis: Long?): LocalDate? {
    if (millis == null || millis <= 0) {
        return null
    }

    val instant = Instant.ofEpochMilli(millis)
    val zoneId = ZoneId.systemDefault()

    return instant.atZone(zoneId).toLocalDate()
}

fun epochMillisToLocalDateTime(millis: Long?): ZonedDateTime? {
    if (millis == null) {
        return null
    }

    val instant = Instant.ofEpochMilli(millis)
    val zoneId = ZoneId.systemDefault()

    return instant.atZone(zoneId)
}

fun formatEpochMillis(millis: Long, format: String): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())

    return dateFormat.format(Date(millis))
}

fun genCode(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    return (1..length)
        .map { allowedChars.random(Random) }
        .joinToString("")
}