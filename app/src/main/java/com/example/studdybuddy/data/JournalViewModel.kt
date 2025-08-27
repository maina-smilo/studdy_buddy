package com.example.studdybuddy.data

    import android.content.Context
    import android.widget.Toast
    import androidx.lifecycle.ViewModel
    import androidx.navigation.NavController
    import com.example.studdybuddy.models.JournalEntry
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import java.text.SimpleDateFormat
    import java.util.*

    class JournalViewModel : ViewModel() {

        fun uploadJournalEntry(
            title: String,
            mood: String,
            content: String,
            context: Context,
            navController: NavController
        ) {
            val database = FirebaseDatabase.getInstance().getReference("JournalEntries")

            val entryId = database.push().key ?: return

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            val journalEntry = mapOf(
                "id" to entryId,
                "title" to title,
                "mood" to mood,
                "content" to content,
                "date" to currentDate
            )

            database.child(entryId).setValue(journalEntry)
                .addOnSuccessListener {
                    Toast.makeText(context, "Journal entry saved!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack() // Go back to previous screen
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error saving journal entry", Toast.LENGTH_SHORT).show()
                }
        }
        fun getJournalEntryById(id: String, callback: (JournalEntry?) -> Unit) {
            val database = FirebaseDatabase.getInstance().getReference("JournalEntries")
            database.child(id).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val entry = snapshot.getValue(JournalEntry::class.java)
                        callback(entry)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null)
                    }
                }
            )
        }



        // ✅ Update an existing journal entry
        fun updateJournalEntry(
            id: String,
            title: String,
            mood: String,
            content: String,
            context: Context,
            navController: NavController
        ) {
            val database = FirebaseDatabase.getInstance().getReference("JournalEntries")

            val updatedEntry = mapOf(
                "title" to title,
                "mood" to mood,
                "content" to content
            )

            database.child(id).updateChildren(updatedEntry)
                .addOnSuccessListener {
                    Toast.makeText(context, "Journal updated!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update journal", Toast.LENGTH_SHORT).show()
                }
        }

    }

