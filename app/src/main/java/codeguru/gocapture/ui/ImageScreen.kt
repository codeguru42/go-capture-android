package codeguru.gocapture.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import codeguru.gocapture.R
import coil.compose.AsyncImage

@Composable
fun ImageScreen(navController: NavHostController, modifier: Modifier, imageUri: String?) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { }
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
