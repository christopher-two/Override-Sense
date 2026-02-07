package org.override.sense.feature.monitor.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.override.sense.feature.monitor.domain.SoundCategory

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
