package org.override.sense.feature.monitor.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.override.sense.feature.monitor.domain.SoundCategory

@Composable
fun PulseAnimation(
    isScanning: Boolean,
    currentCategory: SoundCategory?,
    modifier: Modifier = Modifier
) {
    if (!isScanning) return

    val (waveCount, baseColor) = when (currentCategory) {
        SoundCategory.CRITICAL -> 4 to MaterialTheme.colorScheme.error
        SoundCategory.WARNING -> 3 to MaterialTheme.colorScheme.tertiary
        SoundCategory.INFO -> 2 to MaterialTheme.colorScheme.primary
        null -> 1 to MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Create multiple waves based on intensity
        for (i in 0 until waveCount) {
            PulseWave(
                delay = i * (2000 / waveCount),
                color = baseColor,
                duration = if (currentCategory == SoundCategory.CRITICAL) 1000 else 2000
            )
        }
    }
}

@Composable
private fun PulseWave(
    delay: Int,
    color: Color,
    duration: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = size.minDimension / 2 * scale,
            style = Stroke(width = 4.dp.toPx())
        )
    }
}
