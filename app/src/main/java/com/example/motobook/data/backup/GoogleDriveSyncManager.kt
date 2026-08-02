package com.example.motobook.data.backup

import android.content.Context
import com.example.motobook.data.local.database.MotoBookDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object GoogleDriveSyncManager {

    private val client = OkHttpClient()
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    /**
     * Uploads or updates the MotoBook JSON backup file in user's Google Drive.
     */
    suspend fun uploadBackupToDrive(
        accessToken: String,
        database: MotoBookDatabase
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val jsonContent = AutoBackupManager.generateBackupJson(database)

            // 1. Check if backup file already exists on Drive
            val searchRequest = Request.Builder()
                .url("https://www.googleapis.com/drive/v3/files?q=name%3D%27motobook_cloud_backup.json%27+and+trashed%3Dfalse")
                .header("Authorization", "Bearer $accessToken")
                .get()
                .build()

            val searchResponse = client.newCall(searchRequest).execute()
            val searchResponseBody = searchResponse.body?.string() ?: ""

            var existingFileId: String? = null
            if (searchResponse.isSuccessful) {
                val json = JSONObject(searchResponseBody)
                val files = json.optJSONArray("files")
                if (files != null && files.length() > 0) {
                    existingFileId = files.getJSONObject(0).optString("id")
                }
            }

            if (existingFileId != null) {
                // Update existing file content on Drive
                val updateRequest = Request.Builder()
                    .url("https://www.googleapis.com/upload/drive/v3/files/$existingFileId?uploadType=media")
                    .header("Authorization", "Bearer $accessToken")
                    .patch(jsonContent.toRequestBody("application/json".toMediaType()))
                    .build()

                val updateResponse = client.newCall(updateRequest).execute()
                if (updateResponse.isSuccessful) {
                    Result.success("Backup successfully updated on Google Drive!")
                } else {
                    Result.failure(Exception("Drive update failed: ${updateResponse.code}"))
                }
            } else {
                // Create new file via multipart upload
                val metadataJson = JSONObject().apply {
                    put("name", "motobook_cloud_backup.json")
                    put("mimeType", "application/json")
                }.toString()

                val multipartBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "metadata",
                        null,
                        metadataJson.toRequestBody("application/json; charset=UTF-8".toMediaType())
                    )
                    .addFormDataPart(
                        "file",
                        "motobook_cloud_backup.json",
                        jsonContent.toRequestBody("application/json".toMediaType())
                    )
                    .build()

                val createRequest = Request.Builder()
                    .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
                    .header("Authorization", "Bearer $accessToken")
                    .post(multipartBody)
                    .build()

                val createResponse = client.newCall(createRequest).execute()
                if (createResponse.isSuccessful) {
                    Result.success("New backup created on Google Drive!")
                } else {
                    Result.failure(Exception("Drive upload failed: ${createResponse.code}"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Downloads and restores database from Google Drive.
     */
    suspend fun restoreBackupFromDrive(
        accessToken: String,
        database: MotoBookDatabase
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 1. Find file id
            val searchRequest = Request.Builder()
                .url("https://www.googleapis.com/drive/v3/files?q=name%3D%27motobook_cloud_backup.json%27+and+trashed%3Dfalse")
                .header("Authorization", "Bearer $accessToken")
                .get()
                .build()

            val searchResponse = client.newCall(searchRequest).execute()
            val searchResponseBody = searchResponse.body?.string() ?: ""

            if (!searchResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to query Google Drive"))
            }

            val json = JSONObject(searchResponseBody)
            val files = json.optJSONArray("files")
            if (files == null || files.length() == 0) {
                return@withContext Result.failure(Exception("No MotoBook backup found on Google Drive."))
            }

            val fileId = files.getJSONObject(0).getString("id")

            // 2. Download file media content
            val downloadRequest = Request.Builder()
                .url("https://www.googleapis.com/drive/v3/files/$fileId?alt=media")
                .header("Authorization", "Bearer $accessToken")
                .get()
                .build()

            val downloadResponse = client.newCall(downloadRequest).execute()
            val downloadedJson = downloadResponse.body?.string()

            if (downloadResponse.isSuccessful && !downloadedJson.isNullOrBlank()) {
                val success = AutoBackupManager.restoreFromBackupJson(database, downloadedJson)
                if (success) {
                    Result.success("Database restored successfully from Google Drive!")
                } else {
                    Result.failure(Exception("Failed to parse backup format."))
                }
            } else {
                Result.failure(Exception("Failed to download file content from Drive."))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
