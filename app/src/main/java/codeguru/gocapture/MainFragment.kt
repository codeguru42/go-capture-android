package codeguru.gocapture

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
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
}

@Composable
fun App(modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CameraButton()
            ImageButton()
        }
    }
}

@Composable
private fun ImageButton() {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            Log.d(MainFragment::class.toString(), "get content result")
            Log.d(MainFragment::class.toString(), it.toString())
        }
    )

    Button(onClick = { launcher.launch("image/*") }) {
        Icon(
            Icons.Filled.Image,
            contentDescription = stringResource(id = R.string.load_image_button)
        )
    }
}

@Composable
private fun CameraButton() {
    val imagesDir = File(LocalContext.current.filesDir, "images")
    if (!imagesDir.exists()) {
        imagesDir.mkdir()
    }
    val file = File(imagesDir, "go_capture.png")
    val imageUri = FileProvider.getUriForFile(
        LocalContext.current,
        BuildConfig.APPLICATION_ID + ".images.provider",
        file
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            Log.d(MainFragment::class.toString(), "take picture result")
            Log.d(MainFragment::class.toString(), it.toString())
        }
    )

    Button(onClick = { launcher.launch(imageUri) }) {
        Icon(
            Icons.Filled.PhotoCamera,
            contentDescription = stringResource(id = R.string.capture_image_button)
        )
    }
}
