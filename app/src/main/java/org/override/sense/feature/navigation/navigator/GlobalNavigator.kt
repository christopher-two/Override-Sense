package org.override.sense.feature.navigation.navigator

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey
import org.override.sense.core.common.route.RouteGlobal

class GlobalNavigator {
    private val _rootBackStack = mutableStateListOf<NavKey>()
    val rootBackStack: List<NavKey> get() = _rootBackStack

    fun back() {
        if(rootBackStack.isEmpty()) return
        _rootBackStack.removeLastOrNull()
    }

    fun back(
        route: RouteGlobal
    ) {
        if(rootBackStack.isEmpty()) return
        _rootBackStack.remove(route)
    }

    fun navigateTo(route: RouteGlobal) {
        _rootBackStack.add(route)
    }

    fun backTo(targetScreen: NavKey) {
        if (_rootBackStack.isEmpty()) return
        if (_rootBackStack.last() == targetScreen) return

        while(_rootBackStack.isNotEmpty() && _rootBackStack.last() != targetScreen){
            _rootBackStack.removeLastOrNull()
        }
    }

    fun clearAndNavigateTo(route: RouteGlobal) {
        _rootBackStack.clear()
        _rootBackStack.add(route)
    }
}