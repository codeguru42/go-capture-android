package codeguru.gocapture

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
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
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("GoCapture", response.toString())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("GoCapture", t.toString())
            }
        })
        inputStream?.close()
    }
}
