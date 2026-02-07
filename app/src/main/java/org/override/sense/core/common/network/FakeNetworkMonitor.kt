package org.override.sense.core.common.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Fake implementation of [NetworkMonitor] for testing purposes.
 * 
 * Allows manual control of network connectivity state during tests.
 * 
 * Example usage:
 * ```
 * val fakeNetworkMonitor = FakeNetworkMonitor()
 * fakeNetworkMonitor.setOnline(false) // Simulate offline state
 * ```
 */
class FakeNetworkMonitor : NetworkMonitor {
    private val _isOnline = MutableStateFlow(true)
    
    override val isOnline: Flow<Boolean> = _isOnline
    
    override fun isCurrentlyOnline(): Boolean = _isOnline.value
    
    /**
     * Sets the network connectivity state for testing.
     * 
     * @param online true to simulate online state, false for offline
     */
    fun setOnline(online: Boolean) {
        _isOnline.value = online
    }
}
