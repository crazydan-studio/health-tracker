package org.crazydan.studio.app.healthtracker.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime

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
        if (period.years > 0) "${period.years} 年" else null,
        if (period.months > 0) "${period.months} 月" else null,
        if (period.days > 0) "${period.days} 天" else null,
    )
        .filter { it != null }
        .joinToString(separator = " ")
}

fun getFullName(familyName: String, givenName: String): String {
    val nameSeparator = if (givenName[0].code in 0..255) " " else ""

    return "${familyName}${nameSeparator}${givenName}"
}

fun toEpochMilli(date: LocalDate, time: LocalTime): Long {
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime.of(date, time, zoneId)

    return zonedDateTime.toInstant().toEpochMilli()
}