package org.override.sense.feature.navigation.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppTab(
    val icon: ImageVector,
) {
    Settings(
        icon = Icons.Outlined.Settings,
    ),
    Monitor(
        icon = Icons.Filled.Hearing,
    )
}