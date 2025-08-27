package com.example.studdybuddy.ui.theme.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun AchievementsScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return
    val database = FirebaseDatabase.getInstance()

    var streak by remember { mutableStateOf(0) }
    var totalSessions by remember { mutableStateOf(0) }
    var longestSession by remember { mutableStateOf(0) }

    // --- Firebase listeners ---
    LaunchedEffect(Unit) {
        val streakRef = database.getReference("Streaks").child(uid)
        val sessionsRef = database.getReference("StudySessions").child(uid).child("stats")

        // Streak
        streakRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                streak = snapshot.child("count").getValue(Int::class.java) ?: 0
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Study stats
        sessionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalSessions = snapshot.child("totalSessions").getValue(Int::class.java) ?: 0
                longestSession = snapshot.child("longestSession").getValue(Int::class.java) ?: 0
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // --- Achievements list ---
    val achievements = listOf(
        Achievement("🔥 First Step", "Complete 1 study day", streak >= 1),
        Achievement("⏳ One Week", "7-day streak", streak >= 7),
        Achievement("🏆 Consistency", "30-day streak", streak >= 30),
        Achievement("📚 Rookie", "Complete 5 study sessions", totalSessions >= 5),
        Achievement("🎯 Dedicated", "Complete 20 study sessions", totalSessions >= 20),
        Achievement("💡 Focus Master", "50 study sessions", totalSessions >= 50),
        Achievement("⏰ Focused Hour", "Study 60+ mins in one session", longestSession >= 60),
        Achievement("⚡ Power Session", "Study 120+ mins in one session", longestSession >= 120)
    )

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text(
            text = "Your Achievements",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(achievements.size) { index ->
                AchievementItem(achievements[index])
            }
        }
    }
}

data class Achievement(
    val title: String,
    val description: String,
    val unlocked: Boolean
)

@Composable
fun AchievementItem(achievement: Achievement) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked) Color(0xFFA5D6A7) else Color(0xFFE0E0E0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (achievement.unlocked) Color(0xFF388E3C) else Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (achievement.unlocked) "✔" else "🔒",
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = achievement.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
