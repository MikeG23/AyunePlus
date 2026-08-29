package com.mike.ayuneplus.domain

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class ScheduledFastWindow(
    val start: LocalDateTime,
    val end: LocalDateTime,
)

object FastingTimeCalculator {

    fun calculateScheduledWindow(
        startDate: LocalDate,
        scheduledStartTime: LocalTime,
        plan: FastingPlan,
    ): ScheduledFastWindow {
        val start = LocalDateTime.of(startDate, scheduledStartTime)
        val end = start.plusHours(plan.fastingHours.toLong())

        return ScheduledFastWindow(start = start, end = end)
    }

    fun getElapsedDuration(
        actualStartTime: Instant,
        currentTime: Instant,
    ): Duration {
        require(!currentTime.isBefore(actualStartTime)) {
            "currentTime cannot be before actualStartTime"
        }

        return Duration.between(actualStartTime, currentTime)
    }

    fun isFastCompleted(
        actualDuration: Duration,
        plan: FastingPlan,
    ): Boolean = actualDuration.toMinutes() >= plan.targetDurationMinutes
}
