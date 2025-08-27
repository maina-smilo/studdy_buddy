package com.example.studdybuddy.ui.theme.screens.register


import com.google.firebase.database.FirebaseDatabase

object FirebaseRefs {
    val journalRef = FirebaseDatabase.getInstance().getReference("JournalEntries")
    val usersRef = FirebaseDatabase.getInstance().getReference("Users")
}
