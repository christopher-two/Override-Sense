package org.override.sense.feature.settings.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.override.sense.feature.settings.data.SettingsRepositoryImpl
import org.override.sense.feature.settings.domain.*
import org.override.sense.feature.settings.presentation.SettingsViewModel

val SettingsModule = module {
    // Repository
    single { SettingsRepositoryImpl(androidContext()) } bind SettingsRepository::class

    // Use Cases
    singleOf(::GetSettingsUseCase)
    singleOf(::UpdateThemeUseCase)
    singleOf(::UpdateColorUseCase)
    singleOf(::UpdateSensitivityUseCase)
    singleOf(::UpdateNotificationPriorityUseCase)
    singleOf(::UpdateVibrationPatternUseCase)
    singleOf(::UpdateVibrationEnabledUseCase)
    singleOf(::UpdateVibrationIntensityUseCase)
    singleOf(::UpdateMicrophoneSensitivityUseCase)
    singleOf(::UpdateShowConfidenceUseCase)
    singleOf(::UpdateBypassDoNotDisturbUseCase)
    singleOf(::UpdateShowOnLockScreenUseCase)
    singleOf(::UpdateEnableNotificationLightsUseCase)
    singleOf(::UpdateNoiseReductionUseCase)
    singleOf(::UpdateMinAmplitudeThresholdUseCase)
    singleOf(::UpdatePlaySoundUseCase)
    singleOf(::UpdateSoundVolumeUseCase)
    singleOf(::UpdateOnlyAlertOnceUseCase)
    singleOf(::UpdateBatteryOptimizationUseCase)
    singleOf(::UpdateWorkOnlyWhenChargingUseCase)
    singleOf(::ClearDataUseCase)
    singleOf(::SettingsUseCases)

    // ViewModel
    viewModelOf(::SettingsViewModel)
}
