package org.override.sense.feature.onboarding.presentation

data class OnboardingState(
    val isCompleted: Boolean = false,
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)