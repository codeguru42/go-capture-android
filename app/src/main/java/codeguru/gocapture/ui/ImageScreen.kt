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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import codeguru.gocapture.GoCaptureRepository
import codeguru.gocapture.R
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ImageScreen(navController: NavHostController, modifier: Modifier, imageUri: String?) {
    val repository = GoCaptureRepository(LocalContext.current as Activity)
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isProcessing = true
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.processImage(Uri.parse(imageUri))
                        isProcessing = false
                    }
                }
            ) {
                Icon(
                    Icons.Filled.Upload,
                    stringResource(id = R.string.upload_image)
                )
            }
        }
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
