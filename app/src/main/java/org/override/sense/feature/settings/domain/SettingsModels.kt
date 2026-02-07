package org.override.sense.feature.settings.domain

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

enum class AppColor {
    BLUE, GREEN, RED, PURPLE, ORANGE
}

data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val color: AppColor = AppColor.BLUE
)
