package org.override.sense.feature.onboarding.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingNavigation(
    pagerState: PagerState,
    pageCount: Int,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingIndicator(
            pageCount = pageCount,
            currentPage = pagerState.currentPage,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            if (pagerState.currentPage > 0) {
                TextButton(onClick = onBack) {
                    Text(text = "Atrás")
                }
            } else {
                Spacer(modifier = Modifier.width(64.dp))
            }

            // Next / Start Button
            Button(
                onClick = {
                    if (pagerState.currentPage < pageCount - 1) {
                        onNext()
                    } else {
                        onComplete()
                    }
                }
            ) {
                Text(
                    text = if (pagerState.currentPage == pageCount - 1) "Comenzar" else "Siguiente"
                )
            }
        }
    }
}
