package org.override.sense.feature.onboarding.domain

import kotlinx.coroutines.flow.Flow

class IsOnboardingCompletedUseCase(
    private val onboardingRepository: OnboardingRepository
) {
    operator fun invoke(): Flow<Boolean> = onboardingRepository.isOnboardingCompleted
}
