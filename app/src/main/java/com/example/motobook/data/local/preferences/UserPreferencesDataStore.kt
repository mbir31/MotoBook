package com.example.motobook.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesDataStore(private val context: Context) {

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val THEME_KEY = stringPreferencesKey("theme")
        val GLASS_INTENSITY_KEY = floatPreferencesKey("glass_intensity")
        val CARD_RADIUS_KEY = floatPreferencesKey("card_radius")
        val ANIMATION_SPEED_KEY = stringPreferencesKey("animation_speed")
        val FONT_SIZE_KEY = stringPreferencesKey("font_size")
        val BACKUP_ENABLED_KEY = booleanPreferencesKey("backup_enabled")
        val LAST_BACKUP_TIME_KEY = longPreferencesKey("last_backup_time")
        val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")
        val SELECTED_BIKE_ID_KEY = longPreferencesKey("selected_bike_id")
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "en"
    }

    val theme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: "FROST_LIGHT"
    }

    val glassIntensity: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[GLASS_INTENSITY_KEY] ?: 0.7f
    }

    val cardRadius: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[CARD_RADIUS_KEY] ?: 20.0f
    }

    val animationSpeed: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[ANIMATION_SPEED_KEY] ?: "NORMAL"
    }

    val fontSize: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[FONT_SIZE_KEY] ?: "MEDIUM"
    }

    val backupEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BACKUP_ENABLED_KEY] ?: true
    }

    val lastBackupTime: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[LAST_BACKUP_TIME_KEY] ?: 0L
    }

    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETE_KEY] ?: false
    }

    val selectedBikeId: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[SELECTED_BIKE_ID_KEY] ?: -1L
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs -> prefs[LANGUAGE_KEY] = lang }
    }

    suspend fun setTheme(themeName: String) {
        context.dataStore.edit { prefs -> prefs[THEME_KEY] = themeName }
    }

    suspend fun setGlassIntensity(intensity: Float) {
        context.dataStore.edit { prefs -> prefs[GLASS_INTENSITY_KEY] = intensity }
    }

    suspend fun setCardRadius(radius: Float) {
        context.dataStore.edit { prefs -> prefs[CARD_RADIUS_KEY] = radius }
    }

    suspend fun setAnimationSpeed(speed: String) {
        context.dataStore.edit { prefs -> prefs[ANIMATION_SPEED_KEY] = speed }
    }

    suspend fun setFontSize(size: String) {
        context.dataStore.edit { prefs -> prefs[FONT_SIZE_KEY] = size }
    }

    suspend fun setBackupEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[BACKUP_ENABLED_KEY] = enabled }
    }

    suspend fun setLastBackupTime(timestamp: Long) {
        context.dataStore.edit { prefs -> prefs[LAST_BACKUP_TIME_KEY] = timestamp }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETE_KEY] = complete }
    }

    suspend fun setSelectedBikeId(id: Long) {
        context.dataStore.edit { prefs -> prefs[SELECTED_BIKE_ID_KEY] = id }
    }
}
