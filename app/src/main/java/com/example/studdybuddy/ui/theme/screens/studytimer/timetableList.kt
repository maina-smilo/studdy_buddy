package com.example.studdybuddy.ui.theme.screens.studytimer


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studdybuddy.utils.calculateDurationInMinutes
import com.example.studdybuddy.data.TimetableViewModel
import com.example.studdybuddy.navigation.ROUTE_STUDY_TIMER
import com.example.studdybuddy.navigation.ROUTE_TIMETABLE_FORM

@Composable
fun TimetableListScreen(
    navController: NavController,
    viewModel: TimetableViewModel  // or provide manually
) {
    val entries by viewModel.entries.collectAsState()


    // Group entries by day
    val groupedEntries = entries.groupBy { it.day }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(ROUTE_TIMETABLE_FORM)
            }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        if (entries.isEmpty()) {
            // No entries yet
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No timetable entries yet. Add one using the + button!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                groupedEntries.forEach { (day, dayEntries) ->
                    item {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray)
                                .padding(8.dp)
                        )
                    }
                    items(dayEntries) { entry ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(entry.title, style = MaterialTheme.typography.titleMedium)
                                    Text("${entry.startTime} - ${entry.endTime}", style = MaterialTheme.typography.bodyMedium)
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Start Session Button
                                Button(onClick = {
                                    val durationMinutes = calculateDurationInMinutes(entry.startTime, entry.endTime)
                                    navController.navigate("studyTimer/${entry.title}/$durationMinutes")
                                }) {
                                    Text("Start Session")
                                }


                                Spacer(modifier = Modifier.width(8.dp))

                                // Optional Delete Button
                                IconButton(onClick = { viewModel.deleteEntry(entry.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}
