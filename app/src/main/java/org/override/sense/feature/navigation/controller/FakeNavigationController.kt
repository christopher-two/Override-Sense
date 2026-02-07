package org.override.sense.feature.navigation.controller

import androidx.navigation3.runtime.NavKey
import org.override.sense.core.common.route.RouteGlobal
import org.override.sense.feature.navigation.utils.AppTab

/**
 * Fake implementation of [NavigationController] for testing.
 *
 * Records all navigation calls and provides verification methods
 * for unit tests to assert navigation behavior without actual navigation.
 *
 * Example usage:
 * ```
 * val fakeNav = FakeNavigationController()
 * viewModel.navigateToBook("123")
 * assertTrue(fakeNav.wasNavigatedToBook("123"))
 * ```
 */
class FakeNavigationController : NavigationController {

    // Navigation history
    private val _navigationHistory = mutableListOf<NavigationEvent>()
    val navigationHistory: List<NavigationEvent> get() = _navigationHistory

    // Current state
    private var _currentTab = AppTab.Monitor
    private val _backStack = mutableListOf<Any>()

    sealed class NavigationEvent {
        data class NavigateTo(val route: RouteGlobal) : NavigationEvent()
        data class NavigateToBook(val bookId: String) : NavigationEvent()
        data object Back : NavigationEvent()
        data class BackTo(val route: RouteGlobal) : NavigationEvent()
        data class ClearAndNavigateTo(val route: RouteGlobal) : NavigationEvent()
        data class SwitchTab(val tab: AppTab) : NavigationEvent()
        data class NavigateInTab(val route: NavKey) : NavigationEvent()
        data object BackInTab : NavigationEvent()
    }

    override fun navigateTo(route: RouteGlobal) {
        _navigationHistory.add(NavigationEvent.NavigateTo(route))
        _backStack.add(route)
    }

    override fun back(): Boolean {
        _navigationHistory.add(NavigationEvent.Back)
        return if (_backStack.isNotEmpty()) {
            _backStack.removeLastOrNull()
            true
        } else {
            false
        }
    }

    override fun backTo(route: RouteGlobal) {
        _navigationHistory.add(NavigationEvent.BackTo(route))
        while (_backStack.isNotEmpty() && _backStack.last() != route) {
            _backStack.removeLastOrNull()
        }
    }

    override fun clearAndNavigateTo(route: RouteGlobal) {
        _navigationHistory.add(NavigationEvent.ClearAndNavigateTo(route))
        _backStack.clear()
        _backStack.add(route)
    }

    override fun switchTab(tab: AppTab) {
        _navigationHistory.add(NavigationEvent.SwitchTab(tab))
        _currentTab = tab
    }

    override fun navigateInTab(route: NavKey) {
        _navigationHistory.add(NavigationEvent.NavigateInTab(route))
    }

    override fun backInTab(): Boolean {
        _navigationHistory.add(NavigationEvent.BackInTab)
        return true
    }

    override fun getCurrentTab(): AppTab {
        return _currentTab
    }

    // Verification helpers

    /**
     * Check if navigation to a specific route occurred.
     */
    fun wasNavigatedTo(route: RouteGlobal): Boolean {
        return _navigationHistory.any {
            it is NavigationEvent.NavigateTo && it.route == route
        }
    }

    /**
     * Check if navigation to a specific book occurred.
     */
    fun wasNavigatedToBook(bookId: String): Boolean {
        return _navigationHistory.any {
            it is NavigationEvent.NavigateToBook && it.bookId == bookId
        }
    }

    /**
     * Check if back navigation occurred.
     */
    fun wasBackCalled(): Boolean {
        return _navigationHistory.any { it is NavigationEvent.Back }
    }

    /**
     * Check if tab switch occurred.
     */
    fun wasSwitchedToTab(tab: AppTab): Boolean {
        return _navigationHistory.any {
            it is NavigationEvent.SwitchTab && it.tab == tab
        }
    }

    /**
     * Clear navigation history for test cleanup.
     */
    fun clearHistory() {
        _navigationHistory.clear()
        _backStack.clear()
    }

    /**
     * Get number of navigation events.
     */
    fun getNavigationCount(): Int = _navigationHistory.size
}
