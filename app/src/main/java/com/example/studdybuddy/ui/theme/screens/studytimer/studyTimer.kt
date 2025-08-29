package com.example.studdybuddy.ui.theme.screens.studytimer

import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studdybuddy.navigation.ROUTE_TIMETABLE_FORM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import android.media.RingtoneManager
import android.media.Ringtone
import android.widget.Button
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.studdybuddy.R

@Composable
fun StudyTimerScreen(navController: NavController,
                     topicArg: String = "",
                     durationMinutes: Int = 0) {
    var topic by remember { mutableStateOf(topicArg) }
    var ringtone: Ringtone? by remember { mutableStateOf(null) }
    var showConfetti by remember { mutableStateOf(false) }
    var customMinutesInput by remember { mutableStateOf("25") }
    var customBreakInput by remember { mutableStateOf("5") }

    var timeLeft by remember { mutableStateOf(durationMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var isBreak by remember { mutableStateOf(false) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return
    val streakRef = database.getReference("Streaks").child(uid)
    val sessionsRef = database.getReference("StudySessions").child(uid)

    val encouragements = listOf(
        "Great job! 🎉",
        "You’re crushing it! 🚀",
        "Keep up the amazing work 💪",
        "Focus level: 100% 🔥",
        "You did it! 🌟"
    )
    LaunchedEffect(Unit) {
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringtone = RingtoneManager.getRingtone(context, defaultUri)
    }
    // -------- Helper: Unlock Achievement --------
    fun unlockAchievement(key: String, title: String) {
        val achievementsRef = database.getReference("Achievements").child(uid)
        achievementsRef.child(key).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                achievementsRef.child(key).setValue(true)
                Toast.makeText(context, "🎉 Achievement Unlocked: $title!", Toast.LENGTH_LONG).show()
            }
        }
    }


    // -------- Save session --------
    fun saveSession(minutes: Int) {
        val today = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        val userSessionsRef = sessionsRef.child("sessions") // keep sessions here
        val statsRef = sessionsRef.child("stats") // keep stats separate

        val sessionId = userSessionsRef.push().key ?: return
        val sessionData = mapOf(
            "id" to sessionId,
            "date" to today,
            "duration" to minutes
        )
        userSessionsRef.child(sessionId).setValue(sessionData)

        // Update stats
        statsRef.get().addOnSuccessListener { snapshot ->
            var totalSessions = snapshot.child("totalSessions").getValue(Int::class.java) ?: 0
            var longestSession = snapshot.child("longestSession").getValue(Int::class.java) ?: 0

            totalSessions += 1
            if (minutes > longestSession) {
                longestSession = minutes
            }

            statsRef.setValue(
                mapOf(
                    "totalSessions" to totalSessions,
                    "longestSession" to longestSession
                )
            )

            // 🔥 Achievement checks
            if (totalSessions >= 5) unlockAchievement("rookie", "Rookie")
            if (totalSessions >= 20) unlockAchievement("dedicated", "Dedicated")
            if (totalSessions >= 50) unlockAchievement("focus_master", "Focus Master")
            if (minutes >= 60) unlockAchievement("focused_hour", "Focused Hour")
            if (minutes >= 120) unlockAchievement("power_session", "Power Session")
        }
    }


    fun updateStreak() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        streakRef.get().addOnSuccessListener { snapshot ->
            val lastDate = snapshot.child("lastDate").getValue(String::class.java)
            var streakCount = snapshot.child("count").getValue(Int::class.java) ?: 0
            if (lastDate != today) {
                streakCount += 1
                streakRef.child("count").setValue(streakCount)
                streakRef.child("lastDate").setValue(today)
            }
        }
    }
    var mediaPlayer: MediaPlayer? = null

    fun playRingtone(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.timer_alarm) // put your ringtone file in res/raw
            mediaPlayer?.start()
        }
    }

    fun stopRingtone() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }
    }



    // -------- Start study/break timer --------
    fun startTimer(minutes: Int, breakMode: Boolean = false) {
        timer?.cancel()
        timeLeft = minutes * 60
        isBreak = breakMode
        timer = object : CountDownTimer((timeLeft * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000).toInt()
            }
            override fun onFinish() {
                isRunning = false
                playRingtone(context)
                if (!isBreak) {
                    saveSession(minutes)
                    updateStreak()
                    Toast.makeText(context, encouragements.random(), Toast.LENGTH_SHORT).show()
                    showConfetti = true
                    // Start break automatically
                    val breakMin = customBreakInput.toIntOrNull() ?: 5
                    startTimer(breakMin, breakMode = true)
                    Toast.makeText(context, "⏸ Break Time! Relax 🛌", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Break over! Back to work 💪", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
        isRunning = true
    }

    fun stopTimer() {
        timer?.cancel()
        isRunning = false
    }
    if (showConfetti) {
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("Fireworks.json"))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = 1,
            restartOnPlay = true,
            isPlaying = showConfetti
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { showConfetti = false }, // tap anywhere to dismiss
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.fillMaxSize()
            )
        }
    }


    // -------- UI --------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Topic input
        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Study Topic") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Custom study + break time
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = customMinutesInput,
                onValueChange = { if (it.all { c -> c.isDigit() }) customMinutesInput = it },
                label = { Text("Study Minutes") },
                singleLine = true,
                modifier = Modifier.width(150.dp)
            )
            OutlinedTextField(
                value = customBreakInput,
                onValueChange = { if (it.all { c -> c.isDigit() }) customBreakInput = it },
                label = { Text("Break Minutes") },
                singleLine = true,
                modifier = Modifier.width(150.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Display timer
        val minutes = floor(timeLeft / 60.0).toInt()
        val seconds = timeLeft % 60
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Controls
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    val minutes = customMinutesInput.toIntOrNull() ?: 25
                    startTimer(minutes, breakMode = false)
                },
                enabled = !isRunning
            ) {
                Text("Start")
            }
            Button(
                onClick = { stopTimer() },
                enabled = isRunning
            ) {
                Text("Stop")
            }
            Button(
                onClick = {
                    stopTimer()
                    val minutes = customMinutesInput.toIntOrNull() ?: 25
                    timeLeft = minutes * 60
                }
            ) {
                Text("Reset")
            }


        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {navController.navigate(ROUTE_TIMETABLE_FORM)}) { Text("enter timetable") }

        }

    }
}
