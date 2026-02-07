package org.override.sense.feature.settings.presentation

import org.override.sense.feature.settings.domain.AppColor
import org.override.sense.feature.settings.domain.AppTheme
import org.override.sense.feature.settings.domain.MonitorSettings

data class SettingsState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val color: AppColor = AppColor.BLUE,
    val monitorSettings: MonitorSettings = MonitorSettings(),
    val isLoading: Boolean = false
)