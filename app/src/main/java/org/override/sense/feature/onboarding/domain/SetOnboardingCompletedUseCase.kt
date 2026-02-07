package org.override.sense.feature.onboarding.domain

class SetOnboardingCompletedUseCase(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke() = onboardingRepository.setOnboardingCompleted()
}
