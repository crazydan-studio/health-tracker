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

fun genCode(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    return (1..length)
        .map { allowedChars.random(Random) }
        .joinToString("")
}

/** @param toDayEnd 在 [time] 未指定时，是否将 [time] 设定为 23:59:59.999 */
fun toEpochMillis(date: LocalDate, time: LocalTime? = null, toDayEnd: Boolean = false): Long {
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime.of(
        date,
        time ?: (
                if (toDayEnd) LocalTime.of(23, 59, 59, 999_000_000)
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

/** 格式化："年-月-日" */
const val Pattern_yyyy_MM_dd = "yyyy-MM-dd"

/** 格式化："24小时:分" */
const val Pattern_HH_mm = "HH:mm"

/** 格式化："年-月-日 24小时:分" */
const val Pattern_yyyy_MM_dd_HH_mm = "$Pattern_yyyy_MM_dd $Pattern_HH_mm"

fun formatEpochMillis(millis: Long, format: String): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())

    return dateFormat.format(Date(millis))
}


/**
 * 截取毫秒值为整天
 *
 * @param toDayEnd 是否取当天的结束时间
 */
fun subEpochMillisToDay(millis: Long, toDayEnd: Boolean = false): Long {
    if (millis <= 0) {
        return millis
    }

    val instant = Instant.ofEpochMilli(millis)
    val zoneId = ZoneId.systemDefault()
    val date = instant.atZone(zoneId).toLocalDate()

    val dayStart = date.atStartOfDay(zoneId)

    return (
            if (toDayEnd)
                dayStart
                    .plusDays(1)
                    .minusNanos(1_000_000) // 减去1毫秒
            else
                dayStart
            )
        .toInstant()
        .toEpochMilli()
}