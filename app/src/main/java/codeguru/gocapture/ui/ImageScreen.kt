package codeguru.gocapture.ui

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
    val snackbarHostState = remember { SnackbarHostState() }
    val (errorMessage, setErrorMessage) = remember<MutableState<String?>> { mutableStateOf("") }
    val (blackToPlay, setBlackToPlay) = remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            UploadButton(repository, imageUri, blackToPlay, setIsProcessing, setErrorMessage)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            Row {
                Switch(checked = blackToPlay, onCheckedChange = setBlackToPlay)
                Text(if (blackToPlay) stringResource(R.string.black_to_play) else stringResource(R.string.white_to_play))
            }
            Box {
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

    if (!errorMessage.isNullOrEmpty()) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(errorMessage)
        }
    }
}

@Composable
private fun UploadButton(
    repository: GoCaptureRepository,
    imageUri: String?,
    blackToPlay: Boolean,
    setIsProcessing: (Boolean) -> Unit,
    setErrorMessage: (String?) -> Unit,
) {
    val activity = LocalContext.current as MainActivity
    FloatingActionButton(
        onClick = {
            activity.requestNotificationPermission()
            setIsProcessing(true)
            CoroutineScope(Dispatchers.IO).launch {
                repository.processImage(Uri.parse(imageUri), setErrorMessage, blackToPlay)
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
