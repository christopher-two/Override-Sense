package org.override.sense.feature.settings.domain

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

enum class AppColor {
    BLUE, GREEN, RED, PURPLE, ORANGE
}

enum class VibrationPattern {
    NONE,           // Sin vibración
    SHORT,          // Vibración corta (200ms)
    MEDIUM,         // Vibración media (500ms)
    LONG,           // Vibración larga (1000ms)
    DOUBLE,         // Dos vibraciones cortas
    TRIPLE,         // Tres vibraciones cortas
    PULSING         // Patrón pulsante
}

enum class NotificationPriority {
    LOW,            // Prioridad baja, sin sonido
    DEFAULT,        // Prioridad normal
    HIGH,           // Prioridad alta
    MAX             // Prioridad máxima, heads-up
}

enum class SensitivityLevel {
    VERY_LOW,       // Umbral 0.6 - Solo muy seguros
    LOW,            // Umbral 0.5
    MEDIUM,         // Umbral 0.4
    HIGH,           // Umbral 0.3
    VERY_HIGH       // Umbral 0.2 - Detecta casi todo
}

data class MonitorSettings(
    // Configuración de detección
    val sensitivity: SensitivityLevel = SensitivityLevel.MEDIUM,
    val detectionThreshold: Float = 0.3f, // Se calcula desde sensitivity
    
    // Configuración de notificaciones
    val notificationPriority: NotificationPriority = NotificationPriority.MAX,
    val showConfidence: Boolean = false, // Mostrar porcentaje de confianza
    val bypassDoNotDisturb: Boolean = true,
    val showOnLockScreen: Boolean = true,
    val enableNotificationLights: Boolean = true,
    
    // Configuración de vibración
    val vibrationPattern: VibrationPattern = VibrationPattern.DOUBLE,
    val vibrationEnabled: Boolean = true,
    val vibrationIntensity: Float = 1.0f, // 0.0 - 1.0
    
    // Configuración de audio
    val microphoneSensitivity: Float = 1.0f, // Ganancia del micrófono 0.5 - 2.0
    val noiseReduction: Boolean = false, // Reducción de ruido (experimental)
    val minAmplitudeThreshold: Int = 100, // Umbral mínimo de amplitud
    
    // Configuración de alertas
    val playSound: Boolean = true,
    val soundVolume: Float = 1.0f, // 0.0 - 1.0
    val onlyAlertOnce: Boolean = false, // Solo alertar una vez por sonido similar
    
    // Configuración de batería
    val batteryOptimization: Boolean = false, // Pausar en batería baja
    val workOnlyWhenCharging: Boolean = false
)

data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val color: AppColor = AppColor.BLUE,
    val monitorSettings: MonitorSettings = MonitorSettings()
)
