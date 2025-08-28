package com.example.studdybuddy.ui.theme.screens.moodtracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studdybuddy.data.MoodTrackerViewModel
import com.example.studdybuddy.models.MoodEntry
import com.example.studdybuddy.navigation.ROUTE_DASHBOARD
import java.util.*

@Composable
fun MoodTrackerScreen(
    navController: NavController,
    viewModel: MoodTrackerViewModel = viewModel() // inject VM here
) {
    val moods = listOf(
        "😄" to "Happy",
        "😢" to "Sad",
        "😡" to "Angry",
        "😍" to "Love",
        "😖" to "Frustrated",
        "😕" to "Confused",
        "😬" to "Tense",
        "🤢" to "Disgusted"
    )

    var selectedMood by remember { mutableStateOf<Pair<String, String>?>(null) }
    var customMoodName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "How are you feeling today?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Emoji grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.height(200.dp)
        ) {
            items(moods) { mood ->
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            selectedMood = mood
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = mood.first, fontSize = 40.sp)
                    Text(text = mood.second, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Show input & actions only when mood is picked ---
        selectedMood?.let { mood ->
            OutlinedTextField(
                value = customMoodName,
                onValueChange = { customMoodName = it },
                label = { Text("Name this mood (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Would you like to journal this?", fontSize = 16.sp)

            // Save mood + go to Journal
            Button(
                onClick = {
                    val entry = MoodEntry(
                        id = "", // will be filled by push().key
                        emoji = mood.first,
                        name = if (customMoodName.isNotBlank()) customMoodName else mood.second,
                        timestamp = System.currentTimeMillis()
                    )
                    viewModel.addMood(entry) // ✅ save for this user only

                    navController.navigate("journal/${mood.second}/${customMoodName}")
                },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Go to Journal ✏️")
            }

            // Just save mood + go back
            Button(
                onClick = {
                    val entry = MoodEntry(
                        id = "",
                        emoji = mood.first,
                        name = if (customMoodName.isNotBlank()) customMoodName else mood.second,
                        timestamp = System.currentTimeMillis()
                    )
                    viewModel.addMood(entry)

                    navController.navigate(ROUTE_DASHBOARD)
                },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Save & Back")
            }
        }
    }
}
