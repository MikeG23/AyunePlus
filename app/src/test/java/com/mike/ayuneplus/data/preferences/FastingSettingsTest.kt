package com.mike.ayuneplus.data.preferences

import com.mike.ayuneplus.domain.FastingPlan
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class FastingSettingsTest {

    @Test
    fun `default settings use the 16-8 plan starting at 20`() {
        assertEquals(FastingPlan.SIXTEEN_EIGHT, FastingSettings.DEFAULT.plan)
        assertEquals(LocalTime.of(20, 0), FastingSettings.DEFAULT.scheduledStartTime)
    }

    @Test
    fun `settings calculate their scheduled end automatically`() {
        val settings = FastingSettings(
            plan = FastingPlan.FOURTEEN_TEN,
            scheduledStartTime = LocalTime.of(21, 30),
        )

        val window = settings.scheduledWindowFor(LocalDate.of(2026, 8, 28))

        assertEquals(LocalDateTime.of(2026, 8, 29, 11, 30), window.end)
    }
}
