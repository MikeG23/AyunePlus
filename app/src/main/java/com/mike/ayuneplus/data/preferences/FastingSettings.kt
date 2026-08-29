package com.mike.ayuneplus.data.preferences

import com.mike.ayuneplus.domain.FastingPlan
import com.mike.ayuneplus.domain.FastingTimeCalculator
import com.mike.ayuneplus.domain.ScheduledFastWindow
import java.time.LocalDate
import java.time.LocalTime

data class FastingSettings(
    val plan: FastingPlan,
    val scheduledStartTime: LocalTime,
) {
    fun scheduledWindowFor(startDate: LocalDate): ScheduledFastWindow =
        FastingTimeCalculator.calculateScheduledWindow(
            startDate = startDate,
            scheduledStartTime = scheduledStartTime,
            plan = plan,
        )

    companion object {
        val DEFAULT = FastingSettings(
            plan = FastingPlan.SIXTEEN_EIGHT,
            scheduledStartTime = LocalTime.of(20, 0),
        )
    }
}
