package com.mike.ayuneplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mike.ayuneplus.data.preferences.FastingPreferencesRepository
import com.mike.ayuneplus.ui.settings.FastingSettingsRoute
import com.mike.ayuneplus.ui.settings.FastingSettingsViewModel
import com.mike.ayuneplus.ui.theme.AyunePlusTheme

class MainActivity : ComponentActivity() {

    private val fastingPreferencesRepository by lazy {
        FastingPreferencesRepository(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AyunePlusTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val settingsViewModel: FastingSettingsViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer {
                                FastingSettingsViewModel(fastingPreferencesRepository)
                            }
                        },
                    )

                    FastingSettingsRoute(viewModel = settingsViewModel)
                }
            }
        }
    }
}
