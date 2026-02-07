package org.override.sense.feature.monitor.domain

import java.time.LocalDateTime

enum class SoundCategory {
    CRITICAL, // Alarms, Sirens
    WARNING,  // Doorbell, Knock
    INFO      // Baby cry, etc.
}

data class SoundEvent(
    val id: String,
    val name: String,
    val category: SoundCategory,
    val confidence: Float,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
