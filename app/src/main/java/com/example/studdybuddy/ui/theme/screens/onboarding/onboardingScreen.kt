package com.example.studdybuddy.ui.theme.screens.onboarding

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studdybuddy.R
import com.example.studdybuddy.navigation.ROUTE_LOGIN
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.example.studdybuddy.navigation.ROUTE_DASHBOARD

@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            R.drawable.ic_timer,
            "Stay Focused ⏱️",
            "Boost your productivity with a custom study timer ⏳. Track every session, avoid distractions, and build the habit of deep focus."
        ),
        OnboardingPage(
            R.drawable.ic_fire,
            "Build Streaks 🔥",
            "Stay consistent by building daily study streaks 📆. Unlock achievements 🏅 and challenge yourself to keep going!"
        ),
        OnboardingPage(
            R.drawable.ic_chat,
            "Chat with Buddy 🤖",
            "Your AI bestie is here for you 💬—crack jokes 😂, drop motivation 💪, or just vibe when you’re feeling down."
        ),
        OnboardingPage(
            R.drawable.ic_book,
            "Track Your Journey 📚",
            "Journal your thoughts 📝, log your moods 😄😢😡, and organize your schedule 📅 with the built-in timetable to stay on top of your goals."
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Skip button at top right ---
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Skip",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable {
                        context.getSharedPreferences("studdybuddy", Context.MODE_PRIVATE)
                            .edit { putBoolean("onboarding_complete", true) }
                        navController.navigate(ROUTE_LOGIN) { popUpTo(0) }
                    },
                color = MaterialTheme.colorScheme.primary
            )
        }

        // --- Pager ---
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnboardingPageUI(page = pages[page])
        }

        // --- Page Indicator ---
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )

        // --- Next / Get Started Button ---
        Button(
            onClick = {
                if (pagerState.currentPage == pages.lastIndex) {
                    // Save onboarding complete flag
                    context.getSharedPreferences("studdybuddy", Context.MODE_PRIVATE)
                        .edit { putBoolean("onboarding_complete", true) }
                    navController.navigate(ROUTE_LOGIN) { popUpTo(ROUTE_DASHBOARD) {inclusive = true} }
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(if (pagerState.currentPage == pages.lastIndex) "Get Started 🚀" else "Next")
        }
    }
}

data class OnboardingPage(val image: Int, val title: String, val description: String)

@Composable
fun OnboardingPageUI(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = painterResource(id = page.image),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(page.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(page.description, style = MaterialTheme.typography.bodyMedium)
    }
}
