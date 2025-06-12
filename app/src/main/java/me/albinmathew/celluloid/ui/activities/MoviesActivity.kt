package me.albinmathew.celluloid.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MoviesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "movies") {
                    composable("movies") {
                        MoviesScreen(onMovieClick = { navController.navigate("details") })
                    }
                    composable("details") {
                        MovieDetailsScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MoviesScreen(onMovieClick: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Movies") }) }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Button(onClick = onMovieClick) {
                Text("Open Details")
            }
        }
    }
}

@Composable
fun MovieDetailsScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("Movie Details") }) }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("Details screen")
        }
    }
}
