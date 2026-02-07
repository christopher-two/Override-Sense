package org.override.sense.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.override.sense.core.common.route.RouteGlobal
import org.override.sense.feature.navigation.controller.NavigationController
import org.override.sense.feature.onboarding.domain.IsOnboardingCompletedUseCase
import org.override.sense.feature.onboarding.domain.SetOnboardingCompletedUseCase

class OnboardingViewModel(
    private val isOnboardingCompletedUseCase: IsOnboardingCompletedUseCase,
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
    private val navigationController: NavigationController
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                checkOnboardingStatus()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OnboardingState()
        )

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            isOnboardingCompletedUseCase().collect { isCompleted ->
                if (isCompleted) {
                    _state.value = _state.value.copy(isCompleted = true)
                    navigationController.clearAndNavigateTo(RouteGlobal.Home)
                }
            }
        }
    }

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.CompleteOnboarding -> {
                completeOnboarding()
            }
            OnboardingAction.SkipOnboarding -> {
                completeOnboarding()
            }
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            setOnboardingCompletedUseCase()
            _state.value = _state.value.copy(isCompleted = true)
            // Navigation will be triggered by the flow collection in checkOnboardingStatus
            // but we can also trigger it here to be safe/faster
             navigationController.clearAndNavigateTo(RouteGlobal.Home)
        }
    }

}