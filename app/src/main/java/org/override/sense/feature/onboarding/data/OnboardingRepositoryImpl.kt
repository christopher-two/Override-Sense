package org.override.sense.feature.onboarding.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.override.sense.feature.onboarding.domain.OnboardingRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_prefs")

class OnboardingRepositoryImpl(
    private val context: Context
) : OnboardingRepository {

    private object PreferencesKeys {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
    }

    override val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDING_COMPLETED] ?: false
        }

    override suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDING_COMPLETED] = true
        }
    }
}
