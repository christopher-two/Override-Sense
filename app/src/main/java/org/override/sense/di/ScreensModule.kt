package org.override.sense.di

import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.sense.MainViewModel
import org.override.sense.core.common.route.RouteGlobal

import org.override.sense.feature.home.presentation.HomeRoot
import org.override.sense.feature.home.presentation.HomeViewModel
import org.override.sense.feature.onboarding.presentation.OnboardingRoot
import org.override.sense.feature.onboarding.presentation.OnboardingViewModel

@OptIn(KoinExperimentalAPI::class)
val ScreensModule: Module
    get() = module {
        viewModelOf(::MainViewModel)
        viewModelOf(::OnboardingViewModel)
        viewModelOf(::HomeViewModel)

        navigation<RouteGlobal.Onboarding> {
            OnboardingRoot(koinViewModel())
        }

        navigation<RouteGlobal.Home> {
            HomeRoot(koinViewModel())
        }
    }