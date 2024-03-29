package codeguru.gocapture.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import codeguru.gocapture.BuildConfig
import codeguru.gocapture.MainActivity
import codeguru.gocapture.R
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MainScreen(navController: NavHostController, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1.0f))
            CameraButton(navController)
            Spacer(modifier = Modifier.weight(1.0f))
            ImageButton(navController)
            Spacer(modifier = Modifier.weight(1.0f))
        }
    }
}

@Composable
private fun ImageButton(navController: NavHostController) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            Log.d(MainActivity::class.toString(), "get content result")
            Log.d(MainActivity::class.toString(), it.toString())
            if (it != null) {
                val encodedImageUrl = URLEncoder.encode(it.toString(), StandardCharsets.UTF_8)
                navController.navigate("image/${encodedImageUrl}", )
            }
        }
    )

    Button(onClick = { launcher.launch("image/*") }) {
        Icon(
            Icons.Filled.Image,
            contentDescription = stringResource(id = R.string.load_image_button),
            modifier = Modifier.size(96.dp)
        )
    }
}

@Composable
private fun CameraButton(navController: NavController) {
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
            Log.d(MainActivity::class.toString(), "take picture result")
            Log.d(MainActivity::class.toString(), it.toString())
            if (it) {
                val encodedImageUrl = URLEncoder.encode(imageUri.toString(), StandardCharsets.UTF_8)
                navController.navigate("image/${encodedImageUrl}", )
            }
        }
    )

    Button(onClick = { launcher.launch(imageUri) }) {
        Icon(
            Icons.Filled.PhotoCamera,
            contentDescription = stringResource(id = R.string.capture_image_button),
            modifier = Modifier.size(96.dp)
        )
    }
}
