package org.override.sense.feature.settings.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.override.sense.feature.settings.data.SettingsRepositoryImpl
import org.override.sense.feature.settings.domain.ClearDataUseCase
import org.override.sense.feature.settings.domain.GetSettingsUseCase
import org.override.sense.feature.settings.domain.SettingsRepository
import org.override.sense.feature.settings.domain.SettingsUseCases
import org.override.sense.feature.settings.domain.UpdateColorUseCase
import org.override.sense.feature.settings.domain.UpdateLanguageUseCase
import org.override.sense.feature.settings.domain.UpdateThemeUseCase
import org.koin.core.module.dsl.viewModelOf
import org.override.sense.feature.settings.presentation.SettingsViewModel

val SettingsModule = module {
    // Repository
    single { SettingsRepositoryImpl(androidContext()) } bind SettingsRepository::class

    // Use Cases
    singleOf(::GetSettingsUseCase)
    singleOf(::UpdateThemeUseCase)
    singleOf(::UpdateLanguageUseCase)
    singleOf(::UpdateColorUseCase)
    singleOf(::ClearDataUseCase)
    singleOf(::SettingsUseCases)

    // ViewModel
    viewModelOf(::SettingsViewModel)
}
