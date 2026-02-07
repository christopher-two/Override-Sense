package org.override.sense.feature.onboarding.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.override.sense.feature.onboarding.data.OnboardingRepositoryImpl
import org.override.sense.feature.onboarding.domain.IsOnboardingCompletedUseCase
import org.override.sense.feature.onboarding.domain.OnboardingRepository
import org.override.sense.feature.onboarding.domain.SetOnboardingCompletedUseCase

val OnboardingModule = module {
    singleOf(::OnboardingRepositoryImpl) bind OnboardingRepository::class
    singleOf(::IsOnboardingCompletedUseCase)
    singleOf(::SetOnboardingCompletedUseCase)
}
