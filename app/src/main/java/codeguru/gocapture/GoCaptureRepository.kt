package codeguru.gocapture

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class GoCaptureRepository(private val activity: Activity, private val view: View) {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.API_BASE_URL)
        .build()

    fun processImage(imageUri: Uri) {
        val inputStream = activity.contentResolver.openInputStream(imageUri)
        val filePart = MultipartBody.Part.createFormData(
            "image",
            imageUri.lastPathSegment,
            RequestBody.create(MediaType.parse("image/*"), inputStream?.readBytes())
        )
        val service = retrofit.create(GoCaptureService::class.java)
        val call = service.captureImage(filePart)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                var input: InputStream? = null
                try {
                    input = response.body()?.byteStream()
                    val contentType = response.headers().get("Content-Type")
                    input?.let { writeSgfFile(it, contentType) }
                } catch (e: Exception) {
                    Log.e("saveFile", e.toString())
                    view?.let { Snackbar.make(it, "Error", Snackbar.LENGTH_LONG) }?.show()
                } finally {
                    input?.close()
                }
                Log.d("GoCapture", response.toString())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("GoCapture", t.toString())
            }
        })
        inputStream?.close()
    }

    private fun writeSgfFile(input: InputStream, contentType: String?) {
        val resolver: ContentResolver = activity.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "board.sgf")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, contentType)
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            var output: OutputStream? = null
            try {
                output = resolver.openOutputStream(uri)
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int?
                while (input.read(buffer).also { read = it } != -1) {
                    read?.let { output?.write(buffer, 0, it) }
                }
                output?.flush()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (output != null) {
                    try {
                        output.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        view?.let { Snackbar.make(it, "File saved", Snackbar.LENGTH_LONG) }?.show()
    }
}
