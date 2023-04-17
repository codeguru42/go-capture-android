package codeguru.gocapture

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit


class GoCaptureRepository(private val activity: Activity) {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.API_BASE_URL)
        .build()

    suspend fun processImage(imageUri: Uri, processingView: View) {
        val imageStream = activity.contentResolver.openInputStream(imageUri)
        imageStream?.let {
            val filePart = MultipartBody.Part.createFormData(
                "image",
                imageUri.lastPathSegment,
                RequestBody.create(MediaType.parse("image/*"), imageStream.readBytes())
            )
            val service = retrofit.create(GoCaptureService::class.java)
            try {
                Log.d("GoCapture", "Uploading image...")
                val preferences = activity.getPreferences(Context.MODE_PRIVATE)
                val fcmRegistrationToken = preferences.getString(
                    activity.getString(R.string.fcm_token_key),
                    null
                )
                Log.d("GoCapture", "token: $fcmRegistrationToken")
                val response = fcmRegistrationToken?.let {
                    val fcmTokenPart = RequestBody.create(MediaType.parse("text/plain"), it)
                    service.captureImageAsync(filePart, fcmTokenPart)
                }

                Log.d("GoCapture", "Response received")
                Log.d("GoCapture", "response: $response")
            } catch (e: Exception) {
                Log.e("GoCapture", "Error: $e")
                Snackbar.make(processingView, "Error: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
            imageStream.close()
        }
    }

    private fun getFilename(imageUri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.ImageColumns.DISPLAY_NAME)
        val cursor: Cursor? = activity.contentResolver.query(imageUri, projection, null, null, null)
        cursor?.let {
            val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            val filename = cursor.getString(nameIndex)
            cursor.close()
            return File(filename).nameWithoutExtension
        }
        return null
    }
}
