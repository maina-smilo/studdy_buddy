package com.example.studdybuddy.ui.theme.screens.jornal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studdybuddy.data.JournalViewModel

@Composable
fun JournalEntryScreen(
    navController: NavController,
    entryId: String = "",
    mood: String = "",
    moodName: String = "",
    moodEmoji: String = "",
    customMoodName: String? = null
) {
    val journalViewModel: JournalViewModel = viewModel()
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var passedmood by remember { mutableStateOf(mood) }


    // Mood list (you can expand this)
    val moods = listOf("😊", "😡", "😢", "😍", "🤢", "😕")

    LaunchedEffect (entryId){
        if (!entryId.isNullOrEmpty()) {
            journalViewModel.getJournalEntryById(entryId) { entry ->
                entry?.let {
                    title = it.title
                    passedmood = it.mood
                    content = it.content
                }
            }
        }
    }
    LaunchedEffect(mood, customMoodName) {
        if (entryId.isEmpty()) { // only for NEW entries
            passedmood = mood
            if (!customMoodName.isNullOrEmpty()) {
                title = customMoodName // optional: set custom mood as title
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF6E3)) // paper-like background
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            if (!mood.isNullOrEmpty()) {
                Text(text = "Mood: $mood")
            }
            if (!customMoodName.isNullOrEmpty()) {
                Text(text = "Custom Name: $customMoodName")
            }

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Mood Picker
            Text("Pick your mood:")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                moods.forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 28.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (passedmood == emoji) Color.Yellow else Color.Transparent)
                            .clickable { passedmood = emoji }
                            .padding(8.dp)
                    )
                }
            }

            // Content Area
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Write your thoughts...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // fills remaining space
                textStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
                maxLines = Int.MAX_VALUE
            )

            // Buttons Row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (entryId.isNullOrEmpty()) {
                            // New entry
                            journalViewModel.uploadJournalEntry(
                                title,
                                passedmood,
                                content,
                                context,
                                navController
                            )
                        } else {
                            // Update existing
                            journalViewModel.updateJournalEntry(
                                entryId,
                                title,
                                passedmood,
                                content,
                                context,
                                navController
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Save")
                }
                Button(
                    onClick = {
                        title = ""
                        passedmood = ""
                        content = ""
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Discard")
                }
            }
        }
    }
}


