package com.example.studdybuddy.navigation


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studdybuddy.ui.theme.screens.achievements.AchievementsScreen
import com.example.studdybuddy.ui.theme.screens.chatbot.ChatbotScreen
import com.example.studdybuddy.ui.theme.screens.dashboard.DashboardScreen
import com.example.studdybuddy.ui.theme.screens.jornal.JournalEntryScreen
import com.example.studdybuddy.ui.theme.screens.jornal.JournalListScreen
import com.example.studdybuddy.ui.theme.screens.login.LoginScreen
import com.example.studdybuddy.ui.theme.screens.moodtracker.MoodTrackerScreen
import com.example.studdybuddy.ui.theme.screens.profile.PresetAvatarsScreen
import com.example.studdybuddy.ui.theme.screens.profile.ProfileScreen
import com.example.studdybuddy.ui.theme.screens.questionnaire.QuestionnaireScreen
import com.example.studdybuddy.ui.theme.screens.register.RegisterScreen
import com.example.studdybuddy.ui.theme.screens.splash.SplashScreen
import com.example.studdybuddy.ui.theme.screens.studytimer.StudyTimerScreen
import com.example.studdybuddy.ui.theme.screens.studytimer.TimetableFormScreen
import com.example.studdybuddy.ui.theme.screens.studytimer.TimetableListScreen


@Composable
fun AppNavHost(navController: NavHostController= rememberNavController(), startDestination: String = ROUTE_SPLASH){
    NavHost(navController = navController, startDestination = startDestination){
        composable(ROUTE_SPLASH){ SplashScreen(navController) }
        composable(ROUTE_REGISTER){ RegisterScreen(navController) }
        composable(ROUTE_DASHBOARD){ DashboardScreen(navController) }
        composable(ROUTE_QUESTIONNAIRE){ QuestionnaireScreen(navController) }
        composable(ROUTE_ACHIEVEMENTS){ AchievementsScreen(navController) }
        composable(ROUTE_MOOD_TRACKER){ MoodTrackerScreen(navController) }
        composable(ROUTE_JOURNAL_LIST){ JournalListScreen(navController) }
        composable(ROUTE_JOURNAL_ENTRY){ JournalEntryScreen(navController) }
        composable("journal/{mood}/{customMoodName}") { backStackEntry ->
            val passedmood = backStackEntry.arguments?.getString("mood") ?: ""
            val customMoodName = backStackEntry.arguments?.getString("customMoodName") ?: ""
            JournalEntryScreen(navController, passedmood, customMoodName)
        }
        composable("journalEntry/{id}?mood={mood}&moodName={moodName}&moodEmoji={moodEmoji}",
            arguments = listOf(
                navArgument("id") { defaultValue = "" },
                navArgument("mood") { defaultValue = "" },
                navArgument("moodName") { defaultValue = "" },
                navArgument("moodEmoji") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val mood = backStackEntry.arguments?.getString("mood") ?: ""
            val moodName = backStackEntry.arguments?.getString("moodName") ?: ""
            val moodEmoji = backStackEntry.arguments?.getString("moodEmoji") ?: ""

            JournalEntryScreen(navController, id, mood, moodName)
        }
        composable(ROUTE_STUDY_TIMER){ StudyTimerScreen(navController) }
        composable(ROUTE_CHATBOT){ ChatbotScreen(navController) }
        composable(ROUTE_TIMETABLE_FORM){ TimetableFormScreen(navController) }
        composable(ROUTE_TIMETABLE_LIST){ TimetableListScreen(navController, viewModel = viewModel()) }
        composable(ROUTE_LOGIN){ LoginScreen(navController) }
        composable(ROUTE_PROFILE){ ProfileScreen(navController) }
        composable(ROUTE_PRESET_AVATARS){ PresetAvatarsScreen(navController) }
        composable(
            "study_timer/{durationMinutes}",
            arguments = listOf(
                navArgument("durationMinutes") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val durationMinutes = backStackEntry.arguments?.getInt("durationMinutes") ?: 0
            StudyTimerScreen(durationMinutes = durationMinutes, navController = navController)
        }







    }

}
