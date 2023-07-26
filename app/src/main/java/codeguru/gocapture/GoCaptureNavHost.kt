package codeguru.gocapture

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import codeguru.gocapture.ui.MainScreen

@Composable
fun GoCaptureNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = Main.route
    ) {
        composable(route = Main.route) {
            MainScreen(navController = navController, modifier = modifier)
        }
    }
}
