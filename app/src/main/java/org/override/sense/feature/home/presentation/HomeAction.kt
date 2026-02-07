package org.override.sense.feature.home.presentation

import org.override.sense.feature.navigation.utils.AppTab

sealed interface HomeAction {
    data class SelectTab(val tab: AppTab) : HomeAction
    data object ToggleMonitoring : HomeAction
}