package org.override.sense.feature.monitor.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Doorbell
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SmokeFree
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.override.sense.feature.monitor.domain.SoundCategory
import org.override.sense.feature.monitor.domain.SoundEvent

@Composable
fun MonitorStatus(
    isScanning: Boolean,
    currentCategory: SoundCategory?,
    modifier: Modifier = Modifier
) {
    val targetColor = if (!isScanning) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    } else {
        when (currentCategory) {
            SoundCategory.CRITICAL -> MaterialTheme.colorScheme.error
            SoundCategory.WARNING -> MaterialTheme.colorScheme.tertiary
            SoundCategory.INFO -> MaterialTheme.colorScheme.primary
            null -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        }
    }

    val color by animateColorAsState(targetValue = targetColor, animationSpec = tween(500), label = "color")

    val icon = if (!isScanning) {
        Icons.Default.MicOff
    } else {
        when (currentCategory) {
            SoundCategory.CRITICAL -> Icons.Filled.Warning
            SoundCategory.WARNING -> Icons.Outlined.NotificationsActive
            SoundCategory.INFO -> Icons.Outlined.Info
            null -> Icons.Default.Hearing
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = color
        )
    }
}

// New overload that accepts SoundEvent for thematic icons
@Composable
fun MonitorStatus(
    isScanning: Boolean,
    lastDetection: SoundEvent?,
    modifier: Modifier = Modifier
) {
    val targetColor = if (!isScanning) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    } else {
        when (lastDetection?.category) {
            SoundCategory.CRITICAL -> MaterialTheme.colorScheme.error
            SoundCategory.WARNING -> MaterialTheme.colorScheme.tertiary
            SoundCategory.INFO -> MaterialTheme.colorScheme.primary
            null -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        }
    }

    val color by animateColorAsState(targetValue = targetColor, animationSpec = tween(500), label = "color")

    val icon = if (!isScanning) {
        Icons.Default.MicOff
    } else {
        getIconForSound(lastDetection?.name)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = color
        )
    }
}

private fun getIconForSound(soundName: String?): ImageVector {
    return when {
        soundName == null -> Icons.Default.Hearing
        
        // CRITICAL - Fire alarms and smoke
        soundName.contains("Alarma de Incendio", ignoreCase = true) -> Icons.Filled.LocalFireDepartment
        soundName.contains("Detector de Humo", ignoreCase = true) -> Icons.Filled.SmokeFree
        
        // CRITICAL - Sirens
        soundName.contains("Sirena", ignoreCase = true) -> Icons.Filled.Warning
        
        // WARNING - Doorbell and knocking
        soundName.contains("Timbre", ignoreCase = true) -> Icons.Filled.Doorbell
        soundName.contains("Ding-dong", ignoreCase = true) -> Icons.Filled.Doorbell
        soundName.contains("Puerta", ignoreCase = true) -> Icons.Filled.Doorbell
        soundName.contains("Zumbador", ignoreCase = true) -> Icons.Filled.NotificationsActive
        
        // INFO - Baby sounds
        soundName.contains("Bebé", ignoreCase = true) -> Icons.Filled.ChildCare
        soundName.contains("Llanto", ignoreCase = true) -> Icons.Filled.ChildCare
        soundName.contains("Risa", ignoreCase = true) -> Icons.Filled.ChildCare
        
        // Default fallback
        else -> Icons.Filled.Sensors
    }
}
