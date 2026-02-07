package org.override.sense.feature.monitor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class MonitorViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(MonitorState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MonitorState()
        )

    fun onAction(action: MonitorAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }

}