package com.mike.ayuneplus.ui.settings

import com.mike.ayuneplus.data.preferences.FastingSettings
import com.mike.ayuneplus.data.preferences.FastingSettingsRepository
import com.mike.ayuneplus.domain.FastingPlan
import java.time.LocalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FastingSettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `repository settings become ready UI state`() = runTest {
        val repository = FakeFastingSettingsRepository()
        val viewModel = FastingSettingsViewModel(repository)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(FastingSettings.DEFAULT, viewModel.uiState.value.settings)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `selecting a plan saves it and updates UI state`() = runTest {
        val repository = FakeFastingSettingsRepository()
        val viewModel = FastingSettingsViewModel(repository)

        viewModel.selectPlan(FastingPlan.EIGHTEEN_SIX)
        advanceUntilIdle()

        assertEquals(FastingPlan.EIGHTEEN_SIX, repository.savedSettings.value.plan)
        assertEquals(FastingPlan.EIGHTEEN_SIX, viewModel.uiState.value.settings.plan)
    }

    @Test
    fun `selecting a start time saves it and updates UI state`() = runTest {
        val repository = FakeFastingSettingsRepository()
        val viewModel = FastingSettingsViewModel(repository)
        val newTime = LocalTime.of(19, 45)

        viewModel.selectScheduledStartTime(newTime)
        advanceUntilIdle()

        assertEquals(newTime, repository.savedSettings.value.scheduledStartTime)
        assertEquals(newTime, viewModel.uiState.value.settings.scheduledStartTime)
    }

    @Test
    fun `a saving failure is exposed to the UI`() = runTest {
        val repository = FakeFastingSettingsRepository(failWrites = true)
        val viewModel = FastingSettingsViewModel(repository)

        viewModel.selectPlan(FastingPlan.TWENTY_FOUR)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    private class FakeFastingSettingsRepository(
        private val failWrites: Boolean = false,
    ) : FastingSettingsRepository {
        val savedSettings = MutableStateFlow(FastingSettings.DEFAULT)
        override val settings: Flow<FastingSettings> = savedSettings

        override suspend fun setPlan(plan: FastingPlan) {
            check(!failWrites) { "Simulated write error" }
            savedSettings.value = savedSettings.value.copy(plan = plan)
        }

        override suspend fun setScheduledStartTime(time: LocalTime) {
            check(!failWrites) { "Simulated write error" }
            savedSettings.value = savedSettings.value.copy(scheduledStartTime = time)
        }
    }
}
