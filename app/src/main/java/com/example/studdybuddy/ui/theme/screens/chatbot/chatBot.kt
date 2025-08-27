package com.example.studdybuddy.ui.theme.screens.chatbot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ChatbotScreen(navController: NavController) {
    var userInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    // Pair<message, isUser> → true = user, false = bot

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat history
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { (msg, isUser) ->
                ChatMessage(msg, isUser)
            }
        }

        // Input box
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type here...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (userInput.isNotBlank()) {
                    // Add user message
                    messages = messages + (userInput to true)

                    // Get bot response
                    val response = getBotResponse(userInput)
                    messages = messages + (response to false)

                    userInput = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatMessage(message: String, isUser: Boolean) {
    val bgColor =
        if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val textColor =
        if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Surface(
                color = bgColor,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = message,
                    color = textColor,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

// --- BOT LOGIC ---
fun getBotResponse(input: String): String {
    val lower = input.lowercase()

    return when {
        listOf("hi", "hey","heyyy", "hello", "yo", "yoooooo","yoooh","sup").any { lower.contains(it) } ->
            listOf("Yooo, wagwan bestie 😏✨", "Sup slime 💀", "Heyyy, missed me?").random()

        listOf("sad", "lonely", "kinda sad").any { lower.contains(it) } ->
            listOf("Cheer up bestie, sadness ain’t rizz 💔", "Bro u got me, what u mean lonely 😩").random()

        listOf("happy", "good","so happy","excited").any { lower.contains(it) } ->
            listOf("OKAYYYY happiness speedrun 🏃💨", "Ur smile brighter than my screen at 3am 🌙").random()

        listOf("bored","i'm booored").any { lower.contains(it) } ->
            listOf("Touch grass challenge 🌱", "Stare at the wall till it answers 👀").random()

        listOf("tired", "sleep", "nap","i'm really sleepy").any { lower.contains(it) } ->
            listOf("Go nap king/queen, even iPhones need charging 🔋", "Sleep = free trial of death 🛌💀").random()

        listOf("joke", "funny", "laugh","lmao").any { lower.contains(it) } ->
            listOf(
                "Why don’t skeletons fight? They don’t have the guts 💀",
                "Parallel lines have so much in common… shame they’ll never meet 😂"
            ).random()

        listOf("u suck", "bad joke", "knock off", "lame","haha, very funny").any { lower.contains(it) } ->
            listOf("Bruh 💀 harsh.", "L + ratio + ur mom joke incoming 👀", "Okay okay I’ll improve my dad joke game 😭").random()

        else -> listOf(
            "Idk what to say but keep grinding 🔥",
            "Real talk tho, u good? 👀",
            "Lowkey don’t understand but I’m here fr 🫂"
        ).random()
    }
}

