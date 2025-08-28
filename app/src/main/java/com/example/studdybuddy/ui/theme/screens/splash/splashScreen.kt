package com.example.studdybuddy.ui.theme.screens.splash

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.studdybuddy.R
import com.example.studdybuddy.navigation.ROUTE_DASHBOARD
import com.example.studdybuddy.navigation.ROUTE_LOGIN
import com.example.studdybuddy.navigation.ROUTE_ONBOARDING
import com.example.studdybuddy.utils.PreferencesHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, context: Context) {
    val splashScreenDuration = 3000L

    LaunchedEffect(Unit) {
        delay(splashScreenDuration)

        if (PreferencesHelper.isOnboardingCompleted(context)) {
            if (FirebaseAuth.getInstance().currentUser != null) {
                navController.navigate(ROUTE_DASHBOARD) { popUpTo(0) }
            } else {
                navController.navigate(ROUTE_LOGIN) { popUpTo(0) }
            }
        } else {
            navController.navigate(ROUTE_ONBOARDING) { popUpTo(0) }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(300.dp)
            )
            Text(text = "StuddyBuddy", color = Color.White, fontSize = 28.sp)
            Text(text = "Your Study Companion", color = Color.White, fontSize = 14.sp)
        }
    }
}
