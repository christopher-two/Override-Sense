package org.override.sense.core.common.network

import kotlinx.coroutines.flow.Flow

/**
 * Interface for monitoring network connectivity status.
 * 
 * Provides real-time network state updates through a Flow and immediate status checks.
 * Useful for implementing offline-first patterns and network-dependent features.
 * 
 * @see ConnectivityManagerNetworkMonitor for Android implementation
 */
interface NetworkMonitor {
    /**
     * Flow that emits the current network connectivity status.
     * 
     * Emits:
     * - `true` when device has active network connectivity
     * - `false` when device is offline or network is unavailable
     * 
     * The flow is hot and will continue emitting as network status changes.
     */
    val isOnline: Flow<Boolean>
    
    /**
     * Checks the current network connectivity status synchronously.
     * 
     * @return true if device has active network connectivity, false otherwise
     */
    fun isCurrentlyOnline(): Boolean
}
