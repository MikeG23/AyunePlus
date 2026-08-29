package com.mike.ayuneplus.ui.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mike.ayuneplus.data.preferences.FastingSettings
import com.mike.ayuneplus.domain.FastingPlan
import com.mike.ayuneplus.ui.theme.AyunePlusTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val referenceDate: LocalDate = LocalDate.of(2000, 1, 1)

@Composable
fun FastingSettingsRoute(
    viewModel: FastingSettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    FastingSettingsScreen(
        uiState = uiState,
        onPlanSelected = viewModel::selectPlan,
        onTimeClick = {
            val currentTime = uiState.settings.scheduledStartTime
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    viewModel.selectScheduledStartTime(LocalTime.of(hour, minute))
                },
                currentTime.hour,
                currentTime.minute,
                true,
            ).show()
        },
        onErrorDismissed = viewModel::clearError,
        modifier = modifier,
    )
}

@Composable
fun FastingSettingsScreen(
    uiState: FastingSettingsUiState,
    onPlanSelected: (FastingPlan) -> Unit,
    onTimeClick: () -> Unit,
    onErrorDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { contentPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            SettingsContent(
                settings = uiState.settings,
                errorMessage = uiState.errorMessage,
                onPlanSelected = onPlanSelected,
                onTimeClick = onTimeClick,
                onErrorDismissed = onErrorDismissed,
                modifier = Modifier.padding(contentPadding),
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun SettingsContent(
    settings: FastingSettings,
    errorMessage: String?,
    onPlanSelected: (FastingPlan) -> Unit,
    onTimeClick: () -> Unit,
    onErrorDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheduledWindow = settings.scheduledWindowFor(referenceDate)
    val endsNextDay = scheduledWindow.end.toLocalDate().isAfter(scheduledWindow.start.toLocalDate())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        Text(
            text = "Configura tu ayuno",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Elige tu objetivo y la hora en que normalmente comenzarás.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Plan de ayuno",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FastingPlan.entries.forEach { plan ->
                FilterChip(
                    selected = settings.plan == plan,
                    onClick = { onPlanSelected(plan) },
                    label = { Text(plan.displayName) },
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Hora habitual de inicio",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onTimeClick) {
            Text(settings.scheduledStartTime.format(timeFormatter))
        }

        Spacer(modifier = Modifier.height(28.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Tu horario programado",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                ScheduleRow(
                    label = "Inicio",
                    value = scheduledWindow.start.toLocalTime().format(timeFormatter),
                )
                Spacer(modifier = Modifier.height(10.dp))
                ScheduleRow(
                    label = "Fin",
                    value = buildString {
                        append(scheduledWindow.end.toLocalTime().format(timeFormatter))
                        if (endsNextDay) append(" · día siguiente")
                    },
                )
                Spacer(modifier = Modifier.height(10.dp))
                ScheduleRow(
                    label = "Objetivo",
                    value = "${settings.plan.fastingHours} horas",
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onErrorDismissed) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FastingSettingsScreenPreview() {
    AyunePlusTheme(dynamicColor = false) {
        FastingSettingsScreen(
            uiState = FastingSettingsUiState(
                settings = FastingSettings.DEFAULT,
                isLoading = false,
            ),
            onPlanSelected = {},
            onTimeClick = {},
            onErrorDismissed = {},
        )
    }
}
