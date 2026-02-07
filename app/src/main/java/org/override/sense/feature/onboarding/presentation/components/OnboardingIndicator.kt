package org.override.sense.feature.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.outlineVariant
) {
    Row(
        modifier = modifier
            .padding(vertical = 32.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        repeat(pageCount) { page ->
            val isSelected = page == currentPage
            val color = if (isSelected) selectedColor else unselectedColor
            val width = if (isSelected) 24.dp else 12.dp

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(width = width, height = 12.dp)
            )

            if (page != pageCount - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
