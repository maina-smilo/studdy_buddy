package com.example.studdybuddy.models

data class TimetableEntry(
    val id: String = "",        // Firebase key
    val title: String = "",
    val day: String = "",       // e.g., "Monday"
    val startTime: String = "", // "08:00 AM"
    val endTime: String = ""    // "09:00 AM"
)

