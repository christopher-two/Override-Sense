package org.override.sense.feature.onboarding.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(
    pages: List<OnboardingPageItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPage(item = pages[pageIndex])
        }
    }
}
