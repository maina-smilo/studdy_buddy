package com.example.studdybuddy.data

import androidx.lifecycle.ViewModel
import com.example.studdybuddy.models.MoodEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MoodTrackerViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val moodRef = database.getReference("MoodTracker").child(uid)

    private val _entries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val entries: StateFlow<List<MoodEntry>> = _entries

    init {
        fetchMoods()
    }

    fun fetchMoods() {
        moodRef.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<MoodEntry>()
            snapshot.children.forEach { child ->
                val entry = child.getValue(MoodEntry::class.java)
                if (entry != null) list.add(entry)
            }
            _entries.value = list.sortedByDescending { it.timestamp }
        }
    }

    fun addMood(entry: MoodEntry) {
        val key = moodRef.push().key ?: return
        val newEntry = entry.copy(id = key)
        moodRef.child(key).setValue(newEntry).addOnSuccessListener {
            fetchMoods()
        }
    }

    fun deleteMood(entryId: String?) {
        if (entryId != null) {
            moodRef.child(entryId).removeValue().addOnSuccessListener {
                fetchMoods()
            }
        }
    }
}
