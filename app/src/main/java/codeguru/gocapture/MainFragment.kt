package codeguru.gocapture

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import codeguru.gocapture.databinding.FragmentMainBinding
import codeguru.gocapture.ui.theme.GoCaptureTheme
import java.io.File


class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GoCaptureTheme {
                    App(modifier = Modifier.fillMaxSize())
                }
            }
        }
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
        binding.imageButton.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun configureCameraButton(binding: FragmentMainBinding) {
        val activity = requireActivity()
        val imagesDir = File(activity.filesDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdir()
        }
        val file = File(imagesDir, "go_capture.png")
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

        binding.cameraButton.setOnClickListener {
            takePicture.launch(imageUri)
        }
    }

}

@Composable
fun App(modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Button(onClick = { }) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = stringResource(id = R.string.capture_image_button)
                )
            }
            Button(onClick = { }) {
                Icon(
                    Icons.Filled.Image,
                    contentDescription = stringResource(id = R.string.load_image_button)
                )
            }
        }
    }
}
