package com.example.studdybuddy.ui.theme.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studdybuddy.R
import com.example.studdybuddy.ui.theme.screens.register.FirebaseRefs
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PresetAvatarsScreen(navController: NavController) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    // list of drawable avatar resource names
    val avatarList = listOf(
        R.drawable.avatar_boy,
        R.drawable.avatar_gojo,
        R.drawable.avatar_girl,
        R.drawable.avatar_night,
        R.drawable.avatar_cowie,
        R.drawable.avatar_cat,
        R.drawable.avatar_puppy,
        R.drawable.avatar_dog,
        R.drawable.avatar_kitty,
        R.drawable.avatar_cow,
        R.drawable.avatar_suguru,
        R.drawable.ic_avatar_placeholder
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose Your Avatar", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(avatarList) { avatarResId ->
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            if (uid != null) {
                                // save the resource name as avatar
                                FirebaseRefs.usersRef.child(uid)
                                    .child("avatar")
                                    .setValue(
                                        // we save the resource name string, not the int
                                        context.resources.getResourceEntryName(avatarResId)
                                    )
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Avatar selected ✅", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack() // go back to ProfileScreen
                                    }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = avatarResId),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        }
    }
}
