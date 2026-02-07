package org.override.sense

import android.app.Application
import androidx.work.Configuration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.override.sense.core.common.di.CoreCommonModule
import org.override.sense.feature.monitor.di.MonitorModule
import org.override.sense.feature.navigation.di.NavModule
import org.override.sense.feature.onboarding.di.OnboardingModule
import org.override.sense.feature.settings.di.SettingsModule
import org.override.sense.di.ScreensModule
import timber.log.Timber

class SenseApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@SenseApplication)
            workManagerFactory()
            modules(
                listOf(
                    CoreCommonModule,
                    NavModule,
                    OnboardingModule,
                    MonitorModule,
                    SettingsModule,
                    ScreensModule
                )
            )
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
