package codeguru.gocapture.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import codeguru.gocapture.R
import coil.compose.AsyncImage

@Composable
fun ImageScreen(navController: NavHostController, modifier: Modifier, imageUri: String?) {
    AsyncImage(
        model = imageUri,
        contentDescription = stringResource(id = R.string.loaded_image)
    )
}
