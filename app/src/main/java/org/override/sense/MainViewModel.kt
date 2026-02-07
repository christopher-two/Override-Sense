package org.override.sense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.override.sense.core.common.route.RouteGlobal
import org.override.sense.feature.onboarding.domain.IsOnboardingCompletedUseCase

class MainViewModel(
    private val isOnboardingCompletedUseCase: IsOnboardingCompletedUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(false)
    val isOnboardingCompleted = _isOnboardingCompleted.asStateFlow()

    init {
        viewModelScope.launch {
            isOnboardingCompletedUseCase().collect { isCompleted ->
                _isOnboardingCompleted.value = isCompleted
                _isLoading.value = false
            }
        }
    }
}
