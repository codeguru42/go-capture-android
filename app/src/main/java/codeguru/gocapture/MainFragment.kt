package codeguru.gocapture

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File


class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            Log.d(MainFragment::class.toString(), "get content result")
            Log.d(MainFragment::class.toString(), uri.toString())
        }

        val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { saved: Boolean ->
            Log.d(MainFragment::class.toString(), "take picture result")
            Log.d(MainFragment::class.toString(), saved.toString())
        }

        val cameraButton: ImageButton = view.findViewById(R.id.camera_button)
        cameraButton.setOnClickListener {
            val activity = requireActivity()
            val file = File(activity.filesDir,"images/go_capture.png")
            val imageUri = FileProvider.getUriForFile(
                activity,
                BuildConfig.APPLICATION_ID + ".images.provider",
                file
            )
            takePicture.launch(imageUri)
        }

        val imageButton: ImageButton = view.findViewById(R.id.image_button)
        imageButton.setOnClickListener {
            getContent.launch("image/*")
        }

        // Inflate the layout for this fragment
        return view
    }
}
