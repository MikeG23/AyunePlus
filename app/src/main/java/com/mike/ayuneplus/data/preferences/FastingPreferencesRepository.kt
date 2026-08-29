package com.mike.ayuneplus.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mike.ayuneplus.domain.FastingPlan
import java.io.IOException
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val DATA_STORE_NAME = "fasting_settings"

private val Context.fastingSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATA_STORE_NAME,
)

class FastingPreferencesRepository(context: Context) : FastingSettingsRepository {

    private val dataStore = context.applicationContext.fastingSettingsDataStore

    override val settings: Flow<FastingSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map(::preferencesToSettings)

    override suspend fun setPlan(plan: FastingPlan) {
        dataStore.edit { preferences ->
            preferences[Keys.PLAN] = plan.name
        }
    }

    override suspend fun setScheduledStartTime(time: LocalTime) {
        dataStore.edit { preferences ->
            preferences[Keys.START_MINUTE_OF_DAY] = time.toSecondOfDay() / SECONDS_PER_MINUTE
        }
    }

    private fun preferencesToSettings(preferences: Preferences): FastingSettings {
        val plan = preferences[Keys.PLAN]
            ?.let { storedName ->
                FastingPlan.entries.firstOrNull { plan -> plan.name == storedName }
            }
            ?: FastingSettings.DEFAULT.plan

        val startTime = preferences[Keys.START_MINUTE_OF_DAY]
            ?.takeIf { it in MINUTE_OF_DAY_RANGE }
            ?.let { minuteOfDay ->
                LocalTime.ofSecondOfDay(minuteOfDay.toLong() * SECONDS_PER_MINUTE)
            }
            ?: FastingSettings.DEFAULT.scheduledStartTime

        return FastingSettings(
            plan = plan,
            scheduledStartTime = startTime,
        )
    }

    private object Keys {
        val PLAN = stringPreferencesKey("fasting_plan")
        val START_MINUTE_OF_DAY = intPreferencesKey("scheduled_start_minute_of_day")
    }

    private companion object {
        const val SECONDS_PER_MINUTE = 60
        val MINUTE_OF_DAY_RANGE = 0 until 24 * 60
    }
}
