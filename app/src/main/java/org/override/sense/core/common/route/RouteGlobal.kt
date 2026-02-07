package org.override.sense.core.common.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RouteGlobal : NavKey {
    @Serializable
    data object Home : RouteGlobal

    @Serializable
    data object Settings : RouteGlobal

    @Serializable
    data object Onboarding : RouteGlobal
}