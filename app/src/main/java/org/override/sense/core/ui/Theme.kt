package org.override.sense.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import org.override.sense.feature.settings.domain.AppColor
import org.override.sense.feature.settings.domain.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SenseTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    appColor: AppColor = AppColor.BLUE,
    content: @Composable () -> Unit
) {
    val isDark = when (appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    val seedColor = when (appColor) {
        AppColor.BLUE -> Color(0xFF2196F3)
        AppColor.GREEN -> Color(0xFF4CAF50)
        AppColor.RED -> Color(0xFFF44336)
        AppColor.PURPLE -> Color(0xFF9C27B0)
        AppColor.ORANGE -> Color(0xFFFF9800)
    }

    DynamicMaterialTheme(
        seedColor = seedColor,
        content = {
            Surface(
                contentColor = MaterialTheme.colorScheme.onBackground,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize(),
                content = content
            )
        },
        isDark = isDark,
        style = PaletteStyle.Expressive,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        animate = true,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )
}