package org.override.sense.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.snapshotFlow
import org.override.sense.feature.navigation.controller.NavigationController
import org.override.sense.feature.navigation.navigator.HomeNavigator

class HomeViewModel(
    private val navigationController: NavigationController,
    private val homeNavigator: HomeNavigator
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(HomeState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeTabChanges()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeState()
        )

    private fun observeTabChanges() {
        viewModelScope.launch {
            snapshotFlow { homeNavigator.currentTab }
                .collect { tab ->
                    _state.value = _state.value.copy(currentTab = tab)
                }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.SelectTab -> {
                navigationController.switchTab(action.tab)
            }
            HomeAction.ToggleMonitoring -> {
                _state.value = _state.value.copy(isMonitoringActive = !_state.value.isMonitoringActive)
            }
        }
    }

}
