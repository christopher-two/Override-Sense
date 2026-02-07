package org.override.sense.feature.onboarding.presentation.components

import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingPageItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val permission: String? = null
)
