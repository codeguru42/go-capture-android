package codeguru.gocapture.ui

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import codeguru.gocapture.GoCaptureRepository
import codeguru.gocapture.MainActivity
import codeguru.gocapture.R
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ImageScreen(navController: NavHostController, modifier: Modifier, imageUri: String?) {
    val repository = GoCaptureRepository(LocalContext.current as Activity)
    val (isProcessing, setIsProcessing) = remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = { UploadButton(setIsProcessing, repository, imageUri) }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            AsyncImage(
                model = imageUri,
                contentDescription = stringResource(id = R.string.loaded_image)
            )
            if (isProcessing) {
                Processing(modifier = modifier)
            }
        }
    }
}

@Composable
private fun UploadButton(
    setIsProcessing: (Boolean) -> Unit,
    repository: GoCaptureRepository,
    imageUri: String?
) {
    val activity = LocalContext.current as MainActivity
    FloatingActionButton(
        onClick = {
            activity.requestNotificationPermission()
            setIsProcessing(true)
            CoroutineScope(Dispatchers.IO).launch {
                repository.processImage(Uri.parse(imageUri))
                setIsProcessing(false)
            }
        }
    ) {
        Icon(
            Icons.Filled.Upload,
            stringResource(id = R.string.upload_image)
        )
    }
}

@Composable
fun Processing(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.0f))
        CircularProgressIndicator()
        Spacer(modifier = Modifier.weight(1.0f))
    }
}
