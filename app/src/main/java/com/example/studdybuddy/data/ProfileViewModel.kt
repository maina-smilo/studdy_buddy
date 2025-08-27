package com.example.studdybuddy.ui.theme.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.studdybuddy.data.CloudinaryRepository


class ProfileViewModel : ViewModel() {
    private val repo = CloudinaryRepository


    fun uploadAvatar(context: Context, uri: Uri, uid: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val url = withContext(Dispatchers.IO) {
                    repo.uploadToCloudinary(context, uri)
                }
                // ✅ Save Cloudinary URL to Firebase
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(uid)
                        .child("avatar")
                        .setValue(url)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
