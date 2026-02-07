package org.override.sense.core.common.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.override.sense.core.common.logging.Logger
import org.override.sense.core.common.logging.TimberLogger
import org.override.sense.core.common.network.ConnectivityManagerNetworkMonitor
import org.override.sense.core.common.network.NetworkMonitor

val CoreCommonModule
    get() = module {
        single<Logger> { TimberLogger() }
        single<NetworkMonitor> { ConnectivityManagerNetworkMonitor(androidContext(), get()) }
    }
