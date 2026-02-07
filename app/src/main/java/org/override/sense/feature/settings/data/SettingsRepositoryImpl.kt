package org.override.sense.feature.settings.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.override.sense.feature.settings.domain.AppColor
import org.override.sense.feature.settings.domain.AppLanguage
import org.override.sense.feature.settings.domain.AppTheme
import org.override.sense.feature.settings.domain.SettingsRepository
import org.override.sense.feature.settings.domain.UserSettings

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val LANGUAGE = stringPreferencesKey("language")
        val COLOR = stringPreferencesKey("color")
    }

    override fun getSettings(): Flow<UserSettings> {
        return context.settingsDataStore.data.map { preferences ->
            val themeName = preferences[Keys.THEME] ?: AppTheme.SYSTEM.name
            val languageName = preferences[Keys.LANGUAGE] ?: AppLanguage.SYSTEM.name
            val colorName = preferences[Keys.COLOR] ?: AppColor.BLUE.name

            UserSettings(
                theme = AppTheme.valueOf(themeName),
                language = AppLanguage.valueOf(languageName),
                color = AppColor.valueOf(colorName)
            )
        }
    }

    override suspend fun updateTheme(theme: AppTheme) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.THEME] = theme.name
        }
    }

    override suspend fun updateLanguage(language: AppLanguage) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.LANGUAGE] = language.name
        }
    }

    override suspend fun updateColor(color: AppColor) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.COLOR] = color.name
        }
    }

    override suspend fun clearAllData() {
        context.settingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
