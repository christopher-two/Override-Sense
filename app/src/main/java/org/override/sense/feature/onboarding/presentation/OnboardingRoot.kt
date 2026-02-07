package org.override.sense.feature.onboarding.presentation

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import org.override.sense.core.ui.SenseTheme
import org.override.sense.feature.onboarding.presentation.components.OnboardingNavigation
import org.override.sense.feature.onboarding.presentation.components.OnboardingPageItem
import org.override.sense.feature.onboarding.presentation.components.OnboardingPager

@Composable
fun OnboardingRoot(
    viewModel: OnboardingViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OnboardingScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
) {
    val pages = mutableListOf(
        OnboardingPageItem(
            title = "Override Sense",
            description = "Su smartphone ahora detecta lo que usted no escucha, actuando como un centinela constante para su seguridad y autonomía diaria.",
            icon = Icons.Filled.Hearing
        ),
        OnboardingPageItem(
            title = "Detección en Tiempo Real",
            description = "Identificación instantánea de patrones críticos como alarmas, sirenas y timbres mediante inteligencia optimizada para su entorno.",
            icon = Icons.Filled.Bolt,
            permission = Manifest.permission.RECORD_AUDIO
        ),
        OnboardingPageItem(
            title = "Respuesta Sensorial",
            description = "Vibraciones inteligentes y alertas visuales de alto contraste diseñadas para que usted pueda \"sentir\" y ver el sonido al instante.",
            icon = Icons.Filled.Notifications
            // Vibration permission is normal and granted at install time, so we don't strictly need to ask.
            // But consistent with user request to "ask", we can skip or include it.
            // Since it's install-time, requesting it does nothing visible but check status (which is granted).
            // So we leave it null or include it for consistency if we wanted to show status.
            // User asked: "y en la siguiente la vibracion".
            // Since it's normal, we don't need a runtime check. I'll omit it to avoid confusion or just let it pass.
        )
    )

    // Add notification permission only for Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pages.add(
            OnboardingPageItem(
                title = "Privacidad Nativa",
                description = "Análisis de audio procesado exclusivamente en su dispositivo. Sin grabaciones ni envío de datos a la nube; lo que sucede en su espacio, ahí se queda.",
                icon = Icons.Filled.Lock,
                permission = Manifest.permission.POST_NOTIFICATIONS
            )
        )
    } else {
        pages.add(
            OnboardingPageItem(
                title = "Privacidad Nativa",
                description = "Análisis de audio procesado exclusivamente en su dispositivo. Sin grabaciones ni envío de datos a la nube; lo que sucede en su espacio, ahí se queda.",
                icon = Icons.Filled.Lock
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    // Permission logic
    val currentPageIndex = pagerState.currentPage
    val currentPermission = pages.getOrNull(currentPageIndex)?.permission

    val permissionState = if (currentPermission != null) {
        rememberPermissionState(permission = currentPermission)
    } else {
        null
    }

    // Effect to auto-advance if permission granted? Maybe annoying. Let's stick to manual "Next".
    // Or, if user grants permission via dialog, we can auto-advance.
    if (permissionState?.status?.isGranted == true) {
        // Optional: Auto-advance logic could go here, but be careful with loops or user intent.
        // For now, let's just let the user click Next again or have Next act as "Continue".
    }

    Scaffold(
        bottomBar = {
            OnboardingNavigation(
                pagerState = pagerState,
                pageCount = pages.size,
                onNext = {
                    if (permissionState != null && !permissionState.status.isGranted) {
                        permissionState.launchPermissionRequest()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                onBack = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                onComplete = {
                     if (permissionState != null && !permissionState.status.isGranted) {
                        permissionState.launchPermissionRequest()
                    } else {
                        onAction(OnboardingAction.CompleteOnboarding)
                    }
                }
            )
        }
    ) { paddingValues ->
        OnboardingPager(
            pages = pages,
            pagerState = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SenseTheme {
        OnboardingScreen(
            state = OnboardingState(),
            onAction = {}
        )
    }
}
