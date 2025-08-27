package com.example.studdybuddy.data

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class StudyTimerViewModel : ViewModel(){
    private val db = FirebaseDatabase.getInstance().getReference("WeeklyProgress")

    fun saveProgress(userId: String, date: String) {
        val ref = db.child(userId)

        ref.get().addOnSuccessListener { snapshot ->
            val progress = snapshot.getValue(WeeklyProgress::class.java) ?: WeeklyProgress()
            val updatedDays = progress.days.toMutableMap()

            // mark today's date as complete
            updatedDays[date] = true

            ref.setValue(WeeklyProgress(updatedDays))
        }
    }
}
