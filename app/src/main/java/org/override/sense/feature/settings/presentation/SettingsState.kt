package org.override.sense.feature.settings.presentation

import org.override.sense.feature.settings.domain.AppColor
import org.override.sense.feature.settings.domain.AppLanguage
import org.override.sense.feature.settings.domain.AppTheme

data class SettingsState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val color: AppColor = AppColor.BLUE,
    val isLoading: Boolean = false
)