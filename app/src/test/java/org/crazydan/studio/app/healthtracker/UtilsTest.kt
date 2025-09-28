package org.crazydan.studio.app.healthtracker

import org.crazydan.studio.app.healthtracker.util.subEpochMillisToDay
import org.crazydan.studio.app.healthtracker.util.toEpochMillis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-28
 */
class UtilsTest {

    @Test
    fun test_subEpochMillisToDay() {
        val nowDate = LocalDate.now()
        val nowTime = LocalTime.now()
        val nowInMillis = toEpochMillis(nowDate, nowTime)
        val clockNowInMillis = Instant.now().toEpochMilli()

        assertTrue(clockNowInMillis - nowInMillis > 0 && clockNowInMillis - nowInMillis < 100)
        assertEquals(
            toEpochMillis(nowDate, toDayEnd = false),
            subEpochMillisToDay(clockNowInMillis, toDayEnd = false),
        )
        assertEquals(
            toEpochMillis(nowDate, toDayEnd = true),
            subEpochMillisToDay(clockNowInMillis, toDayEnd = true),
        )
    }
}