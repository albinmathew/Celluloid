package me.albinmathew.celluloid.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import me.albinmathew.celluloid.ui.theme.CelluloidTheme
import me.albinmathew.celluloid.navigation.Screen
import me.albinmathew.celluloid.ui.movieslist.MoviesScreen
import me.albinmathew.celluloid.ui.moviedetails.MovieDetailsScreen
import me.albinmathew.celluloid.ui.about.AboutScreen // Import AboutScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CelluloidTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MoviesList.route) {
        composable(Screen.MoviesList.route) {
            MoviesScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetails.createRoute(movieId))
                }
            )
        }
        composable(
            route = Screen.MovieDetails.route,
            arguments = listOf(navArgument("movieId") { type = NavType.LongType })
        ) { // backStackEntry is implicitly available if needed, but viewModel handles arg.
            MovieDetailsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CelluloidTheme {
        AppNavigation()
    }
}
