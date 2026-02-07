package org.override.sense

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.override.sense.core.common.di.CoreCommonModule
import org.override.sense.di.ScreensModule
import org.override.sense.feature.navigation.di.NavModule
import org.override.sense.feature.onboarding.di.OnboardingModule
import org.override.sense.feature.settings.di.SettingsModule
import timber.log.Timber

class SenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@SenseApplication)
            modules(
                listOf(
                    CoreCommonModule,
                    NavModule,
                    OnboardingModule,
                    SettingsModule,
                    ScreensModule
                )
            )
        }
    }
}
