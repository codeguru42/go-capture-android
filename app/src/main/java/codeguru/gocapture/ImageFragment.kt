package codeguru.gocapture

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit


class ImageFragment : Fragment() {
    val args: ImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image, container, false)
        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val imageUri = Uri.parse(args.imageUri)
        imageView.setImageURI(imageUri)

        val uploadButton = view.findViewById<FloatingActionButton>(R.id.upload_button)
        uploadButton.setOnClickListener {
            processImage(imageUri)
        }

        return view
    }

    private fun processImage(imageUri: Uri) {
        val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
        val filePart = MultipartBody.Part.createFormData(
            "image",
            imageUri.lastPathSegment,
            RequestBody.create(MediaType.parse("image/*"), inputStream?.readBytes())
        )
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://192.168.1.2:8000/")
            .build()
        val service = retrofit.create(GoCaptureService::class.java)
        val call = service.captureImage(filePart)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                var input: InputStream? = null
                try {
                    input = response.body()?.byteStream()
                    input?.let { writeSgfFile(it) }
                }catch (e:Exception){
                    Log.e("saveFile",e.toString())
                    view?.let { Snackbar.make(it, "Error", Snackbar.LENGTH_LONG) }?.show()
                }
                finally {
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

    private fun writeSgfFile(input: InputStream) {
        val file = File(Environment.getExternalStorageDirectory(), "board.sgf")
        val fos = FileOutputStream(file)
        fos.use { output ->
            val buffer = ByteArray(4 * 1024) // or other buffer size
            var read: Int?
            while (input.read(buffer).also { read = it } != -1) {
                read?.let { output.write(buffer, 0, it) }
            }
            output.flush()
        }
        view?.let { Snackbar.make(it, "File saved", Snackbar.LENGTH_LONG) }?.show()
    }
}
