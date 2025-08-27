package com.example.studdybuddy.data

import androidx.lifecycle.ViewModel
import com.example.studdybuddy.models.TimetableEntry
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimetableViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val userId = "user1" // replace with actual user id
    private val entriesRef = database.getReference("Timetable").child(userId)

    private val _entries = MutableStateFlow<List<TimetableEntry>>(emptyList())
    val entries: StateFlow<List<TimetableEntry>> = _entries

    init {
        getEntries()
    }

    fun getEntries() {
        entriesRef.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<TimetableEntry>()
            snapshot.children.forEach { child ->
                val entry = child.getValue(TimetableEntry::class.java)
                if (entry != null) list.add(entry)
            }
            _entries.value = list.sortedBy { it.day } // optional: sort by day
        }
    }

    fun addEntry(entry: TimetableEntry) {
        val key = entriesRef.push().key ?: return
        val newEntry = entry.copy(id = key)
        entriesRef.child(key).setValue(newEntry).addOnSuccessListener {
            getEntries() // refresh list
        }
    }

    fun updateEntry(entry: TimetableEntry) {
        if (entry.id.isEmpty()) return
        entriesRef.child(entry.id).setValue(entry).addOnSuccessListener {
            getEntries()
        }
    }

    fun deleteEntry(entryId: String?) {
        if (entryId != null) {
            entriesRef.child(entryId).removeValue().addOnSuccessListener {
                getEntries()
            }
        }
    }
}
