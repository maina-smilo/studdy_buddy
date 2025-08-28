package com.example.studdybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.studdybuddy.navigation.AppNavHost
import com.example.studdybuddy.navigation.ROUTE_DASHBOARD
import com.example.studdybuddy.navigation.ROUTE_LOGIN
import com.example.studdybuddy.navigation.ROUTE_ONBOARDING
import com.example.studdybuddy.ui.theme.StuddyBuddyTheme
import com.example.studdybuddy.utils.PreferencesHelper
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StuddyBuddyTheme {
                AppNavHost( )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StuddyBuddyTheme {
        Greeting("Android")
    }
}