package org.override.sense.feature.settings.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.override.sense.core.ui.SenseTheme
import org.override.sense.feature.settings.domain.NotificationPriority
import org.override.sense.feature.settings.domain.SensitivityLevel
import org.override.sense.feature.settings.domain.VibrationPattern
import org.override.sense.feature.settings.presentation.components.ColorSelector
import org.override.sense.feature.settings.presentation.components.DeleteDataCard
import org.override.sense.feature.settings.presentation.components.SettingsDropdown
import org.override.sense.feature.settings.presentation.components.SettingsSection
import org.override.sense.feature.settings.presentation.components.SettingsSlider
import org.override.sense.feature.settings.presentation.components.SettingsSwitch
import org.override.sense.feature.settings.presentation.components.ThemeSelector

@Composable
fun SettingsRoot(
    viewModel: SettingsViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Información") },
            text = { Text("¿Estás seguro de que deseas borrar toda la información de la aplicación? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(SettingsAction.ClearData)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        // Apariencia
        item { SettingsSection("Apariencia") }
        
        item {
            ThemeSelector(
                selectedTheme = state.theme,
                onThemeSelected = { onAction(SettingsAction.UpdateTheme(it)) }
            )
        }

        item {
            ColorSelector(
                selectedColor = state.color,
                onColorSelected = { onAction(SettingsAction.UpdateColor(it)) }
            )
        }

        // Detección
        item { SettingsSection("Detección de Sonido") }
        
        item {
            SettingsDropdown(
                title = "Sensibilidad",
                selectedValue = state.monitorSettings.sensitivity,
                options = SensitivityLevel.entries,
                onValueSelected = { onAction(SettingsAction.UpdateSensitivity(it)) },
                valueFormatter = { 
                    when (it) {
                        SensitivityLevel.VERY_LOW -> "Muy Baja (60%)"
                        SensitivityLevel.LOW -> "Baja (50%)"
                        SensitivityLevel.MEDIUM -> "Media (40%)"
                        SensitivityLevel.HIGH -> "Alta (30%)"
                        SensitivityLevel.VERY_HIGH -> "Muy Alta (20%)"
                    }
                },
                description = "Umbral de confianza mínimo"
            )
        }

        item {
            SettingsSlider(
                title = "Ganancia del Micrófono",
                value = state.monitorSettings.microphoneSensitivity,
                onValueChange = { onAction(SettingsAction.UpdateMicrophoneSensitivity(it)) },
                valueRange = 0.5f..2.0f,
                steps = 14,
                valueFormatter = { String.format("%.1fx", it) },
                description = "Amplificación de entrada de audio"
            )
        }

        item {
            SettingsSlider(
                title = "Umbral de Amplitud",
                value = state.monitorSettings.minAmplitudeThreshold.toFloat(),
                onValueChange = { onAction(SettingsAction.UpdateMinAmplitudeThreshold(it.toInt())) },
                valueRange = 50f..500f,
                steps = 89,
                valueFormatter = { it.toInt().toString() },
                description = "Nivel mínimo para procesar audio"
            )
        }

        item {
            SettingsSwitch(
                title = "Reducción de Ruido",
                checked = state.monitorSettings.noiseReduction,
                onCheckedChange = { onAction(SettingsAction.UpdateNoiseReduction(it)) },
                description = "Filtrar ruido de fondo (experimental)"
            )
        }

        // Notificaciones
        item { SettingsSection("Notificaciones") }

        item {
            SettingsDropdown(
                title = "Prioridad de Notificaciones",
                selectedValue = state.monitorSettings.notificationPriority,
                options = NotificationPriority.entries,
                onValueSelected = { onAction(SettingsAction.UpdateNotificationPriority(it)) },
                valueFormatter = {
                    when (it) {
                        NotificationPriority.LOW -> "Baja (Silencioso)"
                        NotificationPriority.DEFAULT -> "Normal"
                        NotificationPriority.HIGH -> "Alta"
                        NotificationPriority.MAX -> "Máxima"
                    }
                },
                description = "Importancia de las alertas"
            )
        }

        item {
            SettingsSwitch(
                title = "Mostrar % de Confianza",
                checked = state.monitorSettings.showConfidence,
                onCheckedChange = { onAction(SettingsAction.UpdateShowConfidence(it)) },
                description = "Incluir porcentaje en notificaciones"
            )
        }

        item {
            SettingsSwitch(
                title = "Ignorar No Molestar",
                checked = state.monitorSettings.bypassDoNotDisturb,
                onCheckedChange = { onAction(SettingsAction.UpdateBypassDoNotDisturb(it)) },
                description = "Alertar en modo No Molestar"
            )
        }

        item {
            SettingsSwitch(
                title = "Mostrar en Pantalla Bloqueada",
                checked = state.monitorSettings.showOnLockScreen,
                onCheckedChange = { onAction(SettingsAction.UpdateShowOnLockScreen(it)) },
                description = "Ver sin desbloquear"
            )
        }

        item {
            SettingsSwitch(
                title = "Luz LED",
                checked = state.monitorSettings.enableNotificationLights,
                onCheckedChange = { onAction(SettingsAction.UpdateEnableNotificationLights(it)) },
                description = "Parpadeo LED para alertas"
            )
        }

        item {
            SettingsSwitch(
                title = "Alertar Solo Una Vez",
                checked = state.monitorSettings.onlyAlertOnce,
                onCheckedChange = { onAction(SettingsAction.UpdateOnlyAlertOnce(it)) },
                description = "No repetir sonidos similares"
            )
        }

        // Vibración
        item { SettingsSection("Vibración") }

        item {
            SettingsSwitch(
                title = "Vibración Habilitada",
                checked = state.monitorSettings.vibrationEnabled,
                onCheckedChange = { onAction(SettingsAction.UpdateVibrationEnabled(it)) },
                description = "Vibrar al detectar sonidos"
            )
        }

        if (state.monitorSettings.vibrationEnabled) {
            item {
                SettingsDropdown(
                    title = "Patrón de Vibración",
                    selectedValue = state.monitorSettings.vibrationPattern,
                    options = VibrationPattern.entries,
                    onValueSelected = { onAction(SettingsAction.UpdateVibrationPattern(it)) },
                    valueFormatter = {
                        when (it) {
                            VibrationPattern.NONE -> "Sin vibración"
                            VibrationPattern.SHORT -> "Corta"
                            VibrationPattern.MEDIUM -> "Media"
                            VibrationPattern.LONG -> "Larga"
                            VibrationPattern.DOUBLE -> "Doble"
                            VibrationPattern.TRIPLE -> "Triple"
                            VibrationPattern.PULSING -> "Pulsante"
                        }
                    },
                    description = "Estilo de vibración"
                )
            }

            item {
                SettingsSlider(
                    title = "Intensidad de Vibración",
                    value = state.monitorSettings.vibrationIntensity,
                    onValueChange = { onAction(SettingsAction.UpdateVibrationIntensity(it)) },
                    valueRange = 0.0f..1.0f,
                    steps = 9,
                    valueFormatter = { "${(it * 100).toInt()}%" },
                    description = "Fuerza de vibración"
                )
            }
        }

        // Sonido
        item { SettingsSection("Sonido de Alerta") }

        item {
            SettingsSwitch(
                title = "Reproducir Sonido",
                checked = state.monitorSettings.playSound,
                onCheckedChange = { onAction(SettingsAction.UpdatePlaySound(it)) },
                description = "Sonido al detectar"
            )
        }

        if (state.monitorSettings.playSound) {
            item {
                SettingsSlider(
                    title = "Volumen de Alerta",
                    value = state.monitorSettings.soundVolume,
                    onValueChange = { onAction(SettingsAction.UpdateSoundVolume(it)) },
                    valueRange = 0.0f..1.0f,
                    steps = 9,
                    valueFormatter = { "${(it * 100).toInt()}%" },
                    description = "Volumen del sonido"
                )
            }
        }

        // Batería
        item { SettingsSection("Optimización de Batería") }

        item {
            SettingsSwitch(
                title = "Pausar en Batería Baja",
                checked = state.monitorSettings.batteryOptimization,
                onCheckedChange = { onAction(SettingsAction.UpdateBatteryOptimization(it)) },
                description = "Detener si batería < 15%"
            )
        }

        item {
            SettingsSwitch(
                title = "Solo al Cargar",
                checked = state.monitorSettings.workOnlyWhenCharging,
                onCheckedChange = { onAction(SettingsAction.UpdateWorkOnlyWhenCharging(it)) },
                description = "Monitorear solo conectado"
            )
        }

        // Datos
        item { SettingsSection("Datos") }

        item {
            DeleteDataCard(
                onClick = { showDeleteDialog = true }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SenseTheme {
        SettingsScreen(
            state = SettingsState(),
            onAction = {}
        )
    }
}
