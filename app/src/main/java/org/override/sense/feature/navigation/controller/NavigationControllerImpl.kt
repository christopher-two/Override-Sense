package org.override.sense.feature.navigation.controller

import androidx.navigation3.runtime.NavKey
import org.override.sense.core.common.logging.Logger
import org.override.sense.core.common.route.RouteGlobal
import org.override.sense.feature.navigation.navigator.GlobalNavigator
import org.override.sense.feature.navigation.navigator.HomeNavigator
import org.override.sense.feature.navigation.utils.AppTab

/**
 * Production implementation of [org.override.sense.feature.navigation.controller.NavigationController].
 * 
 * Wraps [GlobalNavigator] and [HomeNavigator] to provide a clean,
 * testable navigation API that isolates Navigation 3 experimental APIs.
 * 
 * This implementation delegates all navigation operations to the underlying
 * navigators while adding logging and type safety.
 * 
 * @param globalNavigator Navigator for global screen transitions
 * @param homeNavigator Navigator for tab-based navigation within Home
 * @param logger Logger for tracking navigation events
 */
class NavigationControllerImpl(
    private val globalNavigator: GlobalNavigator,
    private val homeNavigator: HomeNavigator,
    private val logger: Logger
) : NavigationController {
    
    companion object {
        private const val TAG = "NavigationController"
    }
    
    override fun navigateTo(route: RouteGlobal) {
        logger.d(TAG, "Navigating to global route: $route")
        globalNavigator.navigateTo(route)
    }
    
    override fun back(): Boolean {
        logger.d(TAG, "Navigating back (global)")
        globalNavigator.back()
        return globalNavigator.rootBackStack.isNotEmpty()
    }
    
    override fun backTo(route: RouteGlobal) {
        logger.d(TAG, "Navigating back to: $route")
        globalNavigator.backTo(route)
    }

    override fun clearAndNavigateTo(route: RouteGlobal) {
        logger.d(TAG, "Clearing navigation stack and navigating to: $route")
        globalNavigator.clearAndNavigateTo(route)
    }

    override fun switchTab(tab: AppTab) {
        logger.d(TAG, "Switching to tab: $tab")
        homeNavigator.switchTab(tab)
    }
    
    override fun navigateInTab(route: NavKey) {
        logger.d(TAG, "Navigating within tab to: $route")
        homeNavigator.navigateTo(route)
    }
    
    override fun backInTab(): Boolean {
        logger.d(TAG, "Navigating back within tab")
        return homeNavigator.back()
    }
    
    override fun getCurrentTab(): AppTab {
        return homeNavigator.currentTab
    }
}
