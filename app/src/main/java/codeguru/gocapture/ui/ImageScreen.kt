package codeguru.gocapture.ui

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.processImage(Uri.parse(imageUri))
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
        }
    }
}
