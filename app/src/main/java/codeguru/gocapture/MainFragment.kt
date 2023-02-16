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
import androidx.navigation.findNavController
import codeguru.gocapture.databinding.FragmentMainBinding
import java.io.File


class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        val view = binding.root
        configureImageButton(binding)
        configureCameraButton(binding)

        return view
    }

    private fun configureImageButton(binding: FragmentMainBinding) {
        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                Log.d(MainFragment::class.toString(), "get content result")
                Log.d(MainFragment::class.toString(), uri.toString())
                if (uri != null) {
                    val action = MainFragmentDirections.actionImage(uri.toString())
                    binding.root.findNavController().navigate(action)
                }
            }
        val imageButton: ImageButton = binding.imageButton
        imageButton.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun configureCameraButton(binding: FragmentMainBinding) {
        val activity = requireActivity()
        val imagesDir = File(activity.filesDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdir()
        }
        val file = File(imagesDir,"go_capture.png")
        val imageUri = FileProvider.getUriForFile(
            activity,
            BuildConfig.APPLICATION_ID + ".images.provider",
            file
        )
        val takePicture =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { saved: Boolean ->
                Log.d(MainFragment::class.toString(), "take picture result")
                Log.d(MainFragment::class.toString(), saved.toString())
                if (saved) {
                    val action = MainFragmentDirections.actionImage(imageUri.toString())
                    binding.root.findNavController().navigate(action)
                }
            }

        val cameraButton: ImageButton = binding.cameraButton
        cameraButton.setOnClickListener {
            takePicture.launch(imageUri)
        }
    }
}
