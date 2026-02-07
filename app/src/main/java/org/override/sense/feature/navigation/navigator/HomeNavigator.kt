package org.override.sense.feature.navigation.navigator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey
import org.override.sense.core.common.route.RouteHome
import org.override.sense.feature.navigation.utils.AppTab

class HomeNavigator {
    var currentTab by mutableStateOf(AppTab.Monitor)
        private set

    private val stacks = mapOf(
        AppTab.Monitor to mutableStateListOf<NavKey>(RouteHome.Monitor),
        AppTab.Settings to mutableStateListOf<NavKey>(RouteHome.Settings)
    )

    val currentStack: List<NavKey>
        get() = stacks[currentTab] ?: emptyList()


    fun switchTab(tab: AppTab) {
        currentTab = tab
    }

    fun navigateTo(route: NavKey) {
        stacks[currentTab]?.add(route)
    }

    fun back(): Boolean {
        val activeStack = stacks[currentTab] ?: return false

        if (activeStack.size > 1) {
            activeStack.removeAt(activeStack.lastIndex)
            return true
        }

        if (currentTab != AppTab.Monitor) {
            currentTab = AppTab.Monitor
            return true
        }

        return false
    }
}