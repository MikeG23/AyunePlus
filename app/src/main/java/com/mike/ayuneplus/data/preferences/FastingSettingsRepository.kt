package com.mike.ayuneplus.data.preferences

import com.mike.ayuneplus.domain.FastingPlan
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow

interface FastingSettingsRepository {
    val settings: Flow<FastingSettings>

    suspend fun setPlan(plan: FastingPlan)

    suspend fun setScheduledStartTime(time: LocalTime)
}
