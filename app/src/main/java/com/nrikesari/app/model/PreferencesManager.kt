package com.nrikesari.app.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/* -------------------- */
/* DATASTORE */
/* -------------------- */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "nrikesari_settings"
)

/* -------------------- */
/* PREFERENCES MANAGER */
/* -------------------- */

class PreferencesManager(private val context: Context) {

    companion object {

        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

        // Theme selector key
        private val THEME_COLOR_KEY = stringPreferencesKey("theme_color")
    }

    /* -------------------- */
    /* DARK MODE */
    /* -------------------- */

    val darkModeFlow: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[DARK_MODE_KEY] ?: true
        }

    suspend fun setDarkMode(isDark: Boolean) {

        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }

    /* -------------------- */
    /* THEME COLOR */
    /* -------------------- */

    val themeColorFlow: Flow<String> =
        context.dataStore.data.map { preferences ->
            // Default theme when app first launches
            preferences[THEME_COLOR_KEY] ?: "Default"
        }

    suspend fun setThemeColor(theme: String) {

        context.dataStore.edit { preferences ->
            preferences[THEME_COLOR_KEY] = theme
        }
    }
}