package com.example.studdybuddy.ui.theme.screens.splash


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.studdybuddy.navigation.ROUTE_DASHBOARD
import com.example.studdybuddy.navigation.ROUTE_LOGIN
import com.example.studdybuddy.navigation.ROUTE_REGISTER
import com.example.studdybuddy.navigation.ROUTE_SPLASH
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("splash.json"))


    // Navigate after a short delay
    LaunchedEffect(Unit) {
        delay(2500) // keep splash for 2.5 sec
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navController.navigate(ROUTE_REGISTER) {
                popUpTo(ROUTE_SPLASH) {inclusive = true}// clear splash from backstack
            }
        } else {
            navController.navigate(ROUTE_LOGIN) {
                popUpTo(ROUTE_SPLASH) {inclusive = true}
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize().align(Alignment.Center).alpha(0.3f)

        )
    }
}
