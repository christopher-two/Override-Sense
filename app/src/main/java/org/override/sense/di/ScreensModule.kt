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

import org.override.sense.core.common.route.RouteHome
import org.override.sense.feature.monitor.presentation.MonitorRoot
import org.override.sense.feature.monitor.presentation.MonitorViewModel
import org.override.sense.feature.settings.presentation.SettingsRoot
import org.override.sense.feature.settings.presentation.SettingsViewModel

@OptIn(KoinExperimentalAPI::class)
val ScreensModule: Module
    get() = module {
        viewModelOf(::MainViewModel)
        viewModelOf(::OnboardingViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::MonitorViewModel)
        // SettingsViewModel is already in SettingsModule, but we can declare it here too or just rely on SettingsModule.
        // Actually, viewModelOf needs to be where the ViewModel is defined or imported.
        // Since I added SettingsModule to Application, SettingsViewModel is already provided.
        // But for navigation DSL, I need to use it.

        navigation<RouteGlobal.Onboarding> {
            OnboardingRoot(koinViewModel())
        }

        navigation<RouteGlobal.Home> {
            HomeRoot(koinViewModel())
        }

        // Nested Navigation for Home
        navigation<RouteHome.Monitor> {
            MonitorRoot(koinViewModel())
        }
        
        navigation<RouteHome.Settings> {
            SettingsRoot(koinViewModel())
        }
    }