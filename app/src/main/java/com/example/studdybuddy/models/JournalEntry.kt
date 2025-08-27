package com.example.studdybuddy.models

data class JournalEntry (
    var id: String = "",
    var mood: String = "",
    var title: String = "",
    var content: String = "",
var moodEmoji: String = "",
var moodName: String = "",
var date: String = "",
var text: String = "",
var timestamp: Long = System.currentTimeMillis()
)
