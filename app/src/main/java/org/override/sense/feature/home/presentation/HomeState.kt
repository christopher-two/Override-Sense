package org.override.sense.feature.home.presentation

import org.override.sense.feature.navigation.utils.AppTab

data class HomeState(
    val currentTab: AppTab = AppTab.Monitor,
    val isMonitoringActive: Boolean = true,
)