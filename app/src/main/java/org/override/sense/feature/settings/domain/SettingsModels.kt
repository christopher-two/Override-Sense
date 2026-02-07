package org.override.sense.feature.settings.domain

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

enum class AppLanguage {
    SYSTEM, ENGLISH, SPANISH
}

enum class AppColor {
    BLUE, GREEN, RED, PURPLE, ORANGE
}

data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val color: AppColor = AppColor.BLUE
)
