package org.override.sense.feature.onboarding.presentation

sealed interface OnboardingAction {
    data object CompleteOnboarding : OnboardingAction
    data object SkipOnboarding : OnboardingAction
}
