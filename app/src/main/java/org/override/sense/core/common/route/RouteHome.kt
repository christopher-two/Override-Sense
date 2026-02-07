package org.override.sense.core.common.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RouteHome : NavKey {
    @Serializable
    data object Monitor : RouteHome
    @Serializable
    data object Settings : RouteHome
}