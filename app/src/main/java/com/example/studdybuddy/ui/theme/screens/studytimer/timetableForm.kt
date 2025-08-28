package com.example.studdybuddy.ui.theme.screens.studytimer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studdybuddy.models.TimetableEntry
import com.example.studdybuddy.data.TimetableViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableFormScreen(
    navController: NavController,
    existingEntry: TimetableEntry? = null // null if adding new
) {
    val timetableViewModel: TimetableViewModel = viewModel()

    // Title
    var title by remember { mutableStateOf(existingEntry?.title ?: "") }

    // Day
    var day by remember { mutableStateOf(existingEntry?.day ?: "Monday") }
    val days = listOf("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday")

    // Start Time
    var startHour by remember { mutableStateOf(existingEntry?.startTime?.substringBefore(":") ?: "08") }
    var startMinute by remember { mutableStateOf(existingEntry?.startTime?.substringAfter(":")?.substringBefore(" ") ?: "00") }
    var startAmPm by remember { mutableStateOf(existingEntry?.startTime?.substringAfter(" ") ?: "AM") }

    // End Time
    var endHour by remember { mutableStateOf(existingEntry?.endTime?.substringBefore(":") ?: "09") }
    var endMinute by remember { mutableStateOf(existingEntry?.endTime?.substringAfter(":")?.substringBefore(" ") ?: "00") }
    var endAmPm by remember { mutableStateOf(existingEntry?.endTime?.substringAfter(" ") ?: "AM") }

    val hours = (1..12).map { it.toString().padStart(2,'0') }
    val minutes = (0..59).map { it.toString().padStart(2,'0') }
    val amPmOptions = listOf("AM","PM")

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = if(existingEntry == null) "Add Timetable Entry" else "Edit Timetable Entry",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Day Dropdown
        DropdownSelector(
            label = "Day",
            options = days,
            selected = day,
            onSelect = { day = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Start Time Row

                DropdownSelector("Hour",
                    hours,
                    startHour,
                    { startHour = it },
                    Modifier.weight(1f)
                )
        Spacer(modifier = Modifier.height(8.dp))

                DropdownSelector(
                    "Minute",
                    minutes,
                    startMinute,
                    { startMinute = it },
                    Modifier.weight(1f)
                )
        Spacer(modifier = Modifier.height(8.dp))

                DropdownSelector(
                    "AM/PM",
                    amPmOptions,
                    startAmPm,
                    { startAmPm = it },
                    Modifier.weight(1f)
                )


            Spacer(modifier = Modifier.height(8.dp))

            // End Time Row

                DropdownSelector("Hour",
                    hours,
                    endHour,
                    { endHour = it },
                    Modifier.weight(1f)
                )
        Spacer(modifier = Modifier.height(8.dp))


                DropdownSelector(
                    "Minute",
                    minutes,
                    endMinute,
                    { endMinute = it },
                    Modifier.weight(1f)
                )
        Spacer(modifier = Modifier.height(8.dp))

                DropdownSelector(
                    "AM/PM",
                    amPmOptions,
                    endAmPm,
                    { endAmPm = it },
                    Modifier.weight(1f)
                )
            }


        Spacer(modifier = Modifier.height(16.dp))

        // Save / Update Button
        Button(
            onClick = {
                val startTimeStr = "$startHour:$startMinute $startAmPm"
                val endTimeStr = "$endHour:$endMinute $endAmPm"

                val entry = if(existingEntry == null) {
                    TimetableEntry(title = title, day = day, startTime = startTimeStr, endTime = endTimeStr)
                } else {
                    existingEntry.copy(title = title, day = day, startTime = startTimeStr, endTime = endTimeStr)
                }

                if(existingEntry == null) {
                    timetableViewModel.addEntry(entry)
                } else {
                    timetableViewModel.updateEntry(entry)
                }

                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if(existingEntry == null) "Save Entry" else "Update Entry")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Delete Button (only if editing)
        if (existingEntry != null) {
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete Entry", color = Color.White)
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Confirm Delete") },
                    text = { Text("Are you sure you want to delete this entry?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                timetableViewModel.deleteEntry(existingEntry.id)
                                showDeleteDialog = false
                                navController.popBackStack()
                            }
                        ) { Text("Yes", color = Color.Red) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
