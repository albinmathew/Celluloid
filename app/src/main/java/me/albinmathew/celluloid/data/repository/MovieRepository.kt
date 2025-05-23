package me.albinmathew.celluloid.data.repository

import kotlinx.coroutines.flow.Flow
import me.albinmathew.celluloid.data.local.entity.MovieEntity
import me.albinmathew.celluloid.data.util.Resource

interface MovieRepository {
    fun getPopularMovies(page: Int): Flow<Resource<List<MovieEntity>>>
    fun getTopRatedMovies(page: Int): Flow<Resource<List<MovieEntity>>>
    // For fetching details of a specific movie, including reviews and videos.
    // This might involve multiple API calls and combining data.
    // For now, a simple placeholder, actual implementation might be more complex.
    fun getMovieDetails(movieId: Long): Flow<Resource<MovieEntity>>
    fun getFavoriteMovies(): Flow<List<MovieEntity>>
    suspend fun toggleFavoriteStatus(movie: MovieEntity): Resource<Unit>
    // Example for explicit refresh. Could also be used by getPopularMovies/getTopRatedMovies.
    suspend fun refreshMovies(sortOrder: String, page: Int): Resource<Unit>

    // New methods for Movie Details
    fun getMovieReviews(movieId: Long): Flow<Resource<List<ReviewResponseBean>>>
    fun getMovieVideos(movieId: Long): Flow<Resource<List<VideoResponseBean>>>
}
