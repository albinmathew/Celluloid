package me.albinmathew.celluloid.ui.movieslist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import me.albinmathew.celluloid.R // Assuming R class is available
import me.albinmathew.celluloid.data.local.entity.MovieEntity

@Composable
fun MovieItem(
    movie: MovieEntity,
    onMovieClick: (movieId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable { onMovieClick(movie.movieId) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://image.tmdb.org/t/p/w342${movie.posterPath}") // Using posterPath
                .crossfade(true)
                .build(),
            contentDescription = movie.title,
            modifier = Modifier.aspectRatio(2f / 3f), // Typical poster aspect ratio
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground), // Using default placeholder
            error = painterResource(id = R.drawable.ic_launcher_foreground) // Using default error
        )
    }
}
