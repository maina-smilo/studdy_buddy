package com.example.studdybuddy.data

import androidx.lifecycle.ViewModel
import com.example.studdybuddy.models.TimetableEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimetableViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val entriesRef = uid?.let { database.getReference("Timetable").child(it) }

    private val _entries = MutableStateFlow<List<TimetableEntry>>(emptyList())
    val entries: StateFlow<List<TimetableEntry>> = _entries

    init {
        getEntries()
    }

    fun getEntries() {
        if (entriesRef == null) return  // user not logged in
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
        if (entriesRef == null) return
        val key = entriesRef.push().key ?: return
        val newEntry = entry.copy(id = key)
        entriesRef.child(key).setValue(newEntry).addOnSuccessListener {
            getEntries()
        }
    }

    fun updateEntry(entry: TimetableEntry) {
        if (entriesRef == null || entry.id.isEmpty()) return
        entriesRef.child(entry.id).setValue(entry).addOnSuccessListener {
            getEntries()
        }
    }

    fun deleteEntry(entryId: String?) {
        if (entriesRef == null || entryId == null) return
        entriesRef.child(entryId).removeValue().addOnSuccessListener {
            getEntries()
        }
    }
}

