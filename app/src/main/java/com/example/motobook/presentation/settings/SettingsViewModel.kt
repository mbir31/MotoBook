package com.example.motobook.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.motobook.data.local.preferences.UserPreferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    val selectedLanguage: StateFlow<String> = preferences.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val selectedTheme: StateFlow<String> = preferences.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "FROST_LIGHT")

    val glassIntensity: StateFlow<Float> = preferences.glassIntensity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.7f)

    val cardRadius: StateFlow<Float> = preferences.cardRadius
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 20f)

    val backupEnabled: StateFlow<Boolean> = preferences.backupEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val lastBackupTime: StateFlow<Long> = preferences.lastBackupTime
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun setLanguage(lang: String) {
        viewModelScope.launch { preferences.setLanguage(lang) }
    }

    fun setTheme(themeName: String) {
        viewModelScope.launch { preferences.setTheme(themeName) }
    }

    fun setGlassIntensity(intensity: Float) {
        viewModelScope.launch { preferences.setGlassIntensity(intensity) }
    }

    fun setCardRadius(radius: Float) {
        viewModelScope.launch { preferences.setCardRadius(radius) }
    }

    fun setBackupEnabled(enabled: Boolean) {
        viewModelScope.launch { preferences.setBackupEnabled(enabled) }
    }

    class Factory(private val preferences: UserPreferencesDataStore) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(preferences) as T
        }
    }
}
