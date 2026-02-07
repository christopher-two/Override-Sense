package org.override.sense.core.common.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.override.sense.core.common.logging.Logger

/**
 * Android implementation of [NetworkMonitor] using ConnectivityManager.
 * 
 * Monitors network connectivity changes using NetworkCallback and provides:
 * - Real-time network status updates via Flow
 * - Immediate connectivity checks
 * 
 * This implementation uses ConnectivityManager.NetworkCallback to listen for
 * network availability changes and capability updates.
 * 
 * @param context Android application context
 * @param logger Logger for tracking network events
 */
class ConnectivityManagerNetworkMonitor(
    private val context: Context,
    private val logger: Logger
) : NetworkMonitor {
    
    companion object {
        private const val TAG = "ConnectivityManagerNetworkMonitor"
    }
    
    private val connectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    override val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            private val networks = mutableSetOf<Network>()
            
            override fun onAvailable(network: Network) {
                networks.add(network)
                logger.d(TAG, "Network available: $network (total: ${networks.size})")
                trySend(networks.isNotEmpty())
            }
            
            override fun onLost(network: Network) {
                networks.remove(network)
                logger.d(TAG, "Network lost: $network (remaining: ${networks.size})")
                trySend(networks.isNotEmpty())
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val isConnected = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) && networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
                
                if (isConnected) {
                    networks.add(network)
                } else {
                    networks.remove(network)
                }
                
                logger.d(TAG, "Network capabilities changed: $network (online: $isConnected)")
                trySend(networks.isNotEmpty())
            }
        }
        
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(request, callback)
        
        // Emit initial state
        val initialState = isCurrentlyOnline()
        logger.d(TAG, "Initial network state: $initialState")
        trySend(initialState)
        
        awaitClose {
            logger.d(TAG, "Unregistering network callback")
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
    
    override fun isCurrentlyOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
