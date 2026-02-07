package org.override.sense.feature.onboarding.domain

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    val isOnboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted()
}
