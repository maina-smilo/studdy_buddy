package com.example.studdybuddy.ui.theme.screens.jornal

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studdybuddy.models.JournalEntry
import com.example.studdybuddy.navigation.ROUTE_JOURNAL_ENTRY
import com.example.studdybuddy.ui.theme.screens.register.FirebaseRefs
import com.google.firebase.database.*

@Composable
fun JournalListScreen(navController: NavController) {
    var journalList by remember { mutableStateOf<List<JournalEntry>>(emptyList()) }
    var journal by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        FirebaseRefs.journalRef.orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tempList = mutableListOf<JournalEntry>()
                    for (child in snapshot.children) {
                        child.getValue(JournalEntry::class.java)?.let { tempList.add(it) }
                    }
                    journalList = tempList.sortedByDescending { it.timestamp }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(ROUTE_JOURNAL_ENTRY) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(journalList) { entry ->
                JournalListItem(
                    entry = entry,
                    onClick = {},
                    onEdit = {
                        entry.id?.let { id ->
                            navController.navigate(        "journalEntry/${entry.id}?mood=${entry.mood}&moodName=${entry.moodName}&moodEmoji=${entry.moodEmoji}")
                        }
                    }
            ,
                    onDelete = {
                        FirebaseDatabase.getInstance()
                            .getReference("JournalEntries")
                            .child(entry.id )
                            .removeValue()
                    }
                )
            }
        }
    }
}

@Composable
fun JournalListItem(
    entry: JournalEntry,
    onClick: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${entry.moodEmoji}  ${entry.moodName}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = entry.date, style = MaterialTheme.typography.bodyMedium)
            Text(text = entry.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            val preview =
                if (entry.text.length > 100) entry.text.substring(0, 100) + "..." else entry.text
            Text(text = preview, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Edit Button
                IconButton(onClick = { onEdit() }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                // Delete Button
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }

    // --- Delete confirmation dialog ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this journal entry?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    Toast.makeText(context, "Journal entry deleted ✅", Toast.LENGTH_SHORT).show()
                    showDeleteDialog = false
                }) {
                    Text("Yes", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
