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
import org.override.sense.feature.settings.presentation.components.ColorSelector
import org.override.sense.feature.settings.presentation.components.DeleteDataCard
import org.override.sense.feature.settings.presentation.components.LanguageSelector
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

        item {
            LanguageSelector(
                selectedLanguage = state.language,
                onLanguageSelected = { onAction(SettingsAction.UpdateLanguage(it)) }
            )
        }

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
