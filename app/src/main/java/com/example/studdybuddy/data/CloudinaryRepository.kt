package com.example.studdybuddy.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

object CloudinaryRepository {
    private const val CLOUD_NAME = "dz7ejfgbq"
    private const val UPLOAD_PRESET = "studdy_buddy"
    private val client = OkHttpClient()

    suspend fun uploadToCloudinary(context: Context, uri: Uri): String =
        withContext(Dispatchers.IO) {
            val fileBytes = context.contentResolver.openInputStream(uri)?.readBytes()
                ?: throw Exception("Image read failed")

            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file", "image.jpg",
                    RequestBody.create("image/*".toMediaTypeOrNull(), fileBytes)
                )
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/dz7ejfgbq/image/upload")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("Upload failed: ${response.code}")
                val json = response.body?.string() ?: throw Exception("Empty response")

                // 🔥 Extract the secure_url
                Regex("\"secure_url\"\\s*:\\s*\"(.*?)\"")
                    .find(json)?.groupValues?.get(1)?.replace("\\/", "/")
                    ?: throw Exception("secure_url not found")
            }
        }
}
