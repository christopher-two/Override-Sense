package org.override.sense.feature.navigation.di

import org.override.sense.feature.navigation.controller.NavigationController
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.override.sense.feature.navigation.controller.NavigationControllerImpl
import org.override.sense.feature.navigation.navigator.GlobalNavigator
import org.override.sense.feature.navigation.navigator.HomeNavigator

val NavModule
    get() = module {
        // Legacy navigators (kept for RootNavigationWrapper compatibility)
        singleOf(::GlobalNavigator)
        singleOf(::HomeNavigator)
        
        // New NavigationController abstraction
        singleOf(::NavigationControllerImpl) bind NavigationController::class
    }