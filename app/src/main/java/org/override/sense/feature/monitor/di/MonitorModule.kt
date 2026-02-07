package org.override.sense.feature.monitor.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.override.sense.feature.monitor.data.AudioRecorder
import org.override.sense.feature.monitor.data.RealMonitorRepository
import org.override.sense.feature.monitor.data.SoundClassifier
import org.override.sense.feature.monitor.domain.MonitorRepository
import org.override.sense.feature.monitor.presentation.MonitorViewModel

import org.override.sense.core.common.notification.AppNotificationManager
import org.override.sense.core.common.notification.SystemAppNotificationManager
import org.override.sense.feature.monitor.work.MonitorWorker
import org.koin.androidx.workmanager.dsl.worker

val MonitorModule = module {
    singleOf(::AudioRecorder)
    single { SoundClassifier(androidContext()) }
    single<AppNotificationManager> { SystemAppNotificationManager(androidContext()) }
    
    single<MonitorRepository> { RealMonitorRepository(androidContext(), get()) }
    
    // ViewModel
    viewModelOf(::MonitorViewModel)
    
    // Worker
    worker { MonitorWorker(get(), get()) }
}
