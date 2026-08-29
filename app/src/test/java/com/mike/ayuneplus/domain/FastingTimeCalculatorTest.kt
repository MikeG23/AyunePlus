package com.mike.ayuneplus.domain

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FastingTimeCalculatorTest {

    private val referenceDate = LocalDate.of(2026, 8, 28)

    @Test
    fun `16-8 starting at 20 ends at 12 the next day`() {
        val window = FastingTimeCalculator.calculateScheduledWindow(
            startDate = referenceDate,
            scheduledStartTime = LocalTime.of(20, 0),
            plan = FastingPlan.SIXTEEN_EIGHT,
        )

        assertEquals(LocalDateTime.of(2026, 8, 28, 20, 0), window.start)
        assertEquals(LocalDateTime.of(2026, 8, 29, 12, 0), window.end)
    }

    @Test
    fun `18-6 starting at 19 ends at 13 the next day`() {
        val window = FastingTimeCalculator.calculateScheduledWindow(
            startDate = referenceDate,
            scheduledStartTime = LocalTime.of(19, 0),
            plan = FastingPlan.EIGHTEEN_SIX,
        )

        assertEquals(LocalDateTime.of(2026, 8, 29, 13, 0), window.end)
    }

    @Test
    fun `12-12 starting at 8 ends at 20 the same day`() {
        val window = FastingTimeCalculator.calculateScheduledWindow(
            startDate = referenceDate,
            scheduledStartTime = LocalTime.of(8, 0),
            plan = FastingPlan.TWELVE_TWELVE,
        )

        assertEquals(LocalDateTime.of(2026, 8, 28, 20, 0), window.end)
    }

    @Test
    fun `elapsed time uses actual start even when it is before scheduled time`() {
        val actualStart = Instant.parse("2026-08-29T01:35:00Z")
        val currentTime = Instant.parse("2026-08-29T03:05:00Z")

        val elapsed = FastingTimeCalculator.getElapsedDuration(actualStart, currentTime)

        assertEquals(Duration.ofMinutes(90), elapsed)
    }

    @Test
    fun `elapsed time uses actual start even when it is after scheduled time`() {
        val actualStart = Instant.parse("2026-08-29T02:20:00Z")
        val currentTime = Instant.parse("2026-08-29T04:00:00Z")

        val elapsed = FastingTimeCalculator.getElapsedDuration(actualStart, currentTime)

        assertEquals(Duration.ofMinutes(100), elapsed)
    }

    @Test
    fun `exact target duration completes fast`() {
        assertTrue(
            FastingTimeCalculator.isFastCompleted(
                actualDuration = Duration.ofHours(16),
                plan = FastingPlan.SIXTEEN_EIGHT,
            ),
        )
    }

    @Test
    fun `duration above target completes fast`() {
        assertTrue(
            FastingTimeCalculator.isFastCompleted(
                actualDuration = Duration.ofMinutes(975),
                plan = FastingPlan.SIXTEEN_EIGHT,
            ),
        )
    }

    @Test
    fun `duration below target does not complete fast`() {
        assertFalse(
            FastingTimeCalculator.isFastCompleted(
                actualDuration = Duration.ofHours(14),
                plan = FastingPlan.SIXTEEN_EIGHT,
            ),
        )
    }
}
