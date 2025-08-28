package com.example.studdybuddy.ui.theme.screens.dashboard

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.example.studdybuddy.R
import com.example.studdybuddy.navigation.*
import com.example.studdybuddy.ui.theme.screens.achievements.AchievementItem
import com.example.studdybuddy.ui.theme.screens.register.FirebaseRefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return

    var username by remember { mutableStateOf("User") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(uid) {
        FirebaseRefs.usersRef.child(uid).get().addOnSuccessListener { snapshot ->
            username = snapshot.child("username").value as? String ?: "User"
            avatarUrl = snapshot.child("avatar").value as? String?: "User.avatar"
        }
    }
    val database = FirebaseDatabase.getInstance()
    var streak by remember { mutableStateOf(0) }
    var totalSessions by remember { mutableStateOf(0) }
    var longestSession by remember { mutableStateOf(0) }
    var weeklyMinutes by remember { mutableStateOf(List(7) { 0 }) }

    // --- Firebase listeners ---
    LaunchedEffect(Unit) {
        val streakRef = database.getReference("Streaks").child(uid)
        val sessionsRef = database.getReference("StudySessions").child(uid).child("stats")
        val weeklyRef = database.getReference("WeeklyProgress").child(uid)

        streakRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                streak = snapshot.child("count").getValue(Int::class.java) ?: 0
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        sessionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalSessions = snapshot.child("totalSessions").getValue(Int::class.java) ?: 0
                longestSession = snapshot.child("longestSession").getValue(Int::class.java) ?: 0
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        weeklyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = MutableList(7) { 0 }
                snapshot.children.forEachIndexed { index, day ->
                    tempList[index] = day.getValue(Int::class.java) ?: 0
                }
                weeklyMinutes = tempList
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // --- Lottie Background ---
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("Book_loading.json"))



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFB3E5FC), Color(0xFFE1F5FE))
                )
            )
    ) {
        // Background animation
        LottieAnimation(
            composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize().align(Alignment.Center).alpha(0.3f)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (avatarUrl?.startsWith("http") == true) {
                        // Case 1: Cloudinary URL
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "User Avatar",
                            placeholder = painterResource(R.drawable.ic_avatar_placeholder),
                            error = painterResource(R.drawable.ic_avatar_placeholder),
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .clickable { navController.navigate(ROUTE_PROFILE) }
                        )
                    } else {
                        // Case 2: Preset Avatar (string identifier)
                        val avatarRes = when (avatarUrl) {
                            "avatar1" -> R.drawable.avatar_boy
                            "avatar2" -> R.drawable.avatar_cat
                            "avatar3" -> R.drawable.avatar_cow
                            "avatar4" -> R.drawable.avatar_dog
                            "avatar5" -> R.drawable.avatar_kitty
                            "avatar6" -> R.drawable.avatar_girl
                            "avatar7" -> R.drawable.avatar_cowie
                            "avatar8" -> R.drawable.avatar_puppy
                            "avatar9" -> R.drawable.avatar_suguru
                            "avatar10" -> R.drawable.avatar_gojo
                            "avatar11" -> R.drawable.avatar_night
                            else -> R.drawable.ic_avatar_placeholder
                        }
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .clickable { navController.navigate(ROUTE_PROFILE) }
                        )
                    }


                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Welcome, $username 👋",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF333333)
                    )
                }
                    Row {

                        IconButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(ROUTE_LOGIN) {
                                popUpTo(0)
                            }
                        }) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout")
                        }
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto: mainanimo12@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                            }
                            navController.context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Email, contentDescription = "Support")
                        }
                    }
                }
            }

            item {
                // 🔥 Streak display
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFFFF7043),
                                    Color(0xFFD84315)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥 $streak Days",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            item {
                // 📘 Study Stats
                InfoCard(
                    title = "📘 Study Stats",
                    content = {
                        Text("Total Sessions: $totalSessions")
                        Text("Longest Session: $longestSession min")
                    }
                )}
            item {

                // 📅 Weekly Heatmap
                InfoCard(
                    title = "📅 Weekly Progress",
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            weeklyMinutes.forEach { minutes ->
                                val intensity = when {
                                    minutes == 0 -> Color(0xFFE0E0E0)
                                    minutes < 30 -> Color(0xFFB3E5FC)
                                    minutes < 60 -> Color(0xFF4FC3F7)
                                    else -> Color(0xFF0288D1)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(intensity)
                                )
                            }
                        }
                    }
                )
            }
            item {
                // ✨ Feature Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard("Achievements", Icons.Default.PlayArrow, Color(0xFFD1C4E9)) {
                        navController.navigate(ROUTE_ACHIEVEMENTS)
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard("Mood Tracker", Icons.Default.Place, Color(0xFFFFDAB9)) {
                        navController.navigate(ROUTE_MOOD_TRACKER)
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard("Journal", Icons.Default.FavoriteBorder, Color(0xFFA7C7E7)) {
                        navController.navigate(ROUTE_JOURNAL_LIST)
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard("Study Timer", Icons.Default.Person, Color(0xFFA8D5BA)) {
                        navController.navigate(ROUTE_STUDY_TIMER)
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(
                        "Chatbot",
                        Icons.Default.Phone,
                        Color(0xFFFFB6B9),
                        Modifier.fillMaxWidth()
                    ) {
                        navController.navigate(ROUTE_CHATBOT)
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard("Timetable", Icons.Default.Park, Color(0xFFA8D5BA)) {
                        navController.navigate(ROUTE_TIMETABLE_LIST)
                    }
                }

            }
        }
    }
}


@Composable
fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF333333)))
            content()
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
