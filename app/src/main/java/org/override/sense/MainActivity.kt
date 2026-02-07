package org.override.sense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.override.sense.core.ui.SenseTheme
import org.override.sense.feature.navigation.navigator.RootNavigationWrapper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SenseTheme {
                val viewModel: MainViewModel = koinViewModel()
                val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
                val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

                splashScreen.setKeepOnScreenCondition { isLoading }

                if (!isLoading) {
                    RootNavigationWrapper(isLoggedIn = isOnboardingCompleted)
                }
            }
        }
    }
}
