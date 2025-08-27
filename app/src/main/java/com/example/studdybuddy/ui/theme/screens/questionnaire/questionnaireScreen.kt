package com.example.studdybuddy.ui.theme.screens.questionnaire

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studdybuddy.navigation.ROUTE_DASHBOARD

@Composable
fun QuestionnaireScreen(navController: NavController) {
    // State for answers
    var q1Answer by remember { mutableStateOf("") }
    var q2Answer by remember { mutableStateOf("") }
    var q3Answer by remember { mutableStateOf("") }
    var q4Answer by remember { mutableStateOf("") }

    val questionStyle = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Quick Questionnaire", style = MaterialTheme.typography.headlineSmall)

        // Q1
        Text("1. How many hours do you usually study per day?")
        listOf("Less than 1 hour", "1–3 hours", "3+ hours").forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = q1Answer == option,
                    onClick = { q1Answer = option }
                )
                Text(option)
            }
        }

        // Q2
        Text("2. Do you prefer group study or solo study?")
        listOf("Group", "Solo", "Both").forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = q2Answer == option,
                    onClick = { q2Answer = option }
                )
                Text(option)
            }
        }

        // Q3
        Text("3. What’s your main study goal?")
        listOf("Pass exams", "Learn new skills", "Career growth").forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = q3Answer == option,
                    onClick = { q3Answer = option }
                )
                Text(option)
            }
        }
        Text("4. When are you most available for study?")
        listOf("At night", "Early morning", "Mid-morning","Afternoon", "Evening").forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = q4Answer == option,
                    onClick = { q4Answer = option }
                )
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit button
        Button(
            onClick = {
                // TODO: Store answers or send to backend
                navController.navigate(ROUTE_DASHBOARD)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuestionnaireScreenPreview() {
    QuestionnaireScreen(navController = rememberNavController())
}