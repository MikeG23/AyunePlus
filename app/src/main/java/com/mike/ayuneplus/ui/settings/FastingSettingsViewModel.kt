package com.mike.ayuneplus.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mike.ayuneplus.data.preferences.FastingSettings
import com.mike.ayuneplus.data.preferences.FastingSettingsRepository
import com.mike.ayuneplus.domain.FastingPlan
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

data class FastingSettingsUiState(
    val settings: FastingSettings = FastingSettings.DEFAULT,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class FastingSettingsViewModel(
    private val repository: FastingSettingsRepository,
) : ViewModel() {

    private val operationError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<FastingSettingsUiState> = repository.settings
        .map { settings ->
            FastingSettingsUiState(
                settings = settings,
                isLoading = false,
            )
        }
        .catch {
            emit(
                FastingSettingsUiState(
                    isLoading = false,
                    errorMessage = SETTINGS_READ_ERROR,
                ),
            )
        }
        .combine(operationError) { state, error ->
            state.copy(errorMessage = error ?: state.errorMessage)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = FastingSettingsUiState(),
        )

    fun selectPlan(plan: FastingPlan) {
        saveSetting { repository.setPlan(plan) }
    }

    fun selectScheduledStartTime(time: LocalTime) {
        saveSetting { repository.setScheduledStartTime(time) }
    }

    fun clearError() {
        operationError.value = null
    }

    private fun saveSetting(update: suspend () -> Unit) {
        viewModelScope.launch {
            operationError.value = null
            runCatching { update() }
                .onFailure { operationError.value = SETTINGS_WRITE_ERROR }
        }
    }

    private companion object {
        const val SETTINGS_READ_ERROR = "No se pudo cargar la configuración."
        const val SETTINGS_WRITE_ERROR = "No se pudo guardar la configuración."
    }
}
