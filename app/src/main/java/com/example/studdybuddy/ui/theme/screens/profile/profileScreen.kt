package com.example.studdybuddy.ui.theme.screens.profile


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.studdybuddy.R
import com.example.studdybuddy.data.CloudinaryRepository
import com.example.studdybuddy.navigation.ROUTE_DASHBOARD
import com.example.studdybuddy.navigation.ROUTE_PRESET_AVATARS
import com.example.studdybuddy.navigation.ROUTE_PROFILE
import com.example.studdybuddy.ui.theme.screens.register.FirebaseRefs
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var uploading by remember { mutableStateOf(false) }
    val viewModel: ProfileViewModel = viewModel()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf("ic_avatar_placeholder") }

    // Load user data
    LaunchedEffect(uid) {
        if (uid != null) {
            FirebaseRefs.usersRef.child(uid).get().addOnSuccessListener { snapshot ->
                username = snapshot.child("username").value as? String ?: ""
                email = snapshot.child("email").value as? String ?: ""
                age = snapshot.child("age").value as? String ?: ""
                gender = snapshot.child("gender").value as? String ?: ""
                education = snapshot.child("education").value as? String ?: ""
                avatar = snapshot.child("avatar").value as? String ?: "ic_avatar_placeholder"
            }
        }
    }

    // --- Upload Photo from Gallery ---
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploading = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Upload image to Cloudinary
                    val secureUrl = CloudinaryRepository.uploadToCloudinary(context, it)

                    withContext(Dispatchers.Main) {
                        avatar = secureUrl
                        uid?.let { userId ->
                            FirebaseRefs.usersRef.child(userId).child("avatar").setValue(avatar)
                        }
                        Toast.makeText(context, "Avatar updated 🎉", Toast.LENGTH_SHORT).show()
                        uploading = false
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                        uploading = false
                    }
                }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFD1A5A5))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Avatar ---
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (avatar.startsWith("http")) {
                AsyncImage(model = avatar, contentDescription = "Profile Picture")
            } else {
                val resId = context.resources.getIdentifier(avatar, "drawable", context.packageName)
                if (resId != 0) {
                    Image(painter = painterResource(resId), contentDescription = "Avatar")
                } else {
                    Image(painter = painterResource(R.drawable.ic_avatar_placeholder), contentDescription = "Default Avatar")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tap avatar to upload a photo")

        Spacer(modifier = Modifier.height(16.dp))

        // --- Editable Fields ---
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = education, onValueChange = { education = it }, label = { Text("Education") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate(ROUTE_PRESET_AVATARS) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose Preset Avatar")
        }


        // --- Save Button ---
        Button(
            onClick = {
                if (uid != null) {
                    val updates = mapOf(
                        "username" to username,
                        "email" to email,
                        "age" to age,
                        "gender" to gender,
                        "education" to education,
                        "avatar" to avatar
                    )
                    FirebaseRefs.usersRef.child(uid).updateChildren(updates).addOnSuccessListener {
                        Toast.makeText(context, "Profile updated ✅", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUTE_DASHBOARD) {
                            popUpTo(ROUTE_PROFILE) { inclusive = true }
                        }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uploading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Save Changes")
            }
        }
    }
}
