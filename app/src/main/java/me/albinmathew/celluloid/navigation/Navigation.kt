package me.albinmathew.celluloid.navigation

sealed class Screen(val route: String) {
    object MoviesList : Screen("movies_list")
    object MovieDetails : Screen("movie_details/{movieId}") { // Example with argument
        fun createRoute(movieId: Long) = "movie_details/$movieId"
    }
    object About : Screen("about")
}
