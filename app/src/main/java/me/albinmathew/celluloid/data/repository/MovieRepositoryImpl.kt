package me.albinmathew.celluloid.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.albinmathew.celluloid.api.response.MoviesResponseBean
import me.albinmathew.celluloid.api.service.MovieDbService
import me.albinmathew.celluloid.app.CAConstants // Assuming API_KEY is here
import me.albinmathew.celluloid.data.local.dao.MovieDao
import me.albinmathew.celluloid.data.local.entity.MovieEntity
import me.albinmathew.celluloid.data.util.Resource
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieDbService: MovieDbService,
    private val movieDao: MovieDao
) : MovieRepository {

    // Simplified sort constants, actual values from CAConstants if different
    private val SORT_BY_POPULARITY = "popularity.desc"
    private val SORT_BY_TOP_RATED = "vote_average.desc"

    override fun getPopularMovies(page: Int): Flow<Resource<List<MovieEntity>>> =
        getMoviesInternal(SORT_BY_POPULARITY, page) { movieDao.getAllMoviesSortedByPopularity() }

    override fun getTopRatedMovies(page: Int): Flow<Resource<List<MovieEntity>>> =
        getMoviesInternal(SORT_BY_TOP_RATED, page) { movieDao.getAllMoviesSortedByVoteAverage() }

    private fun getMoviesInternal(
        sortBy: String,
        page: Int,
        daoQuery: () -> Flow<List<MovieEntity>>
    ): Flow<Resource<List<MovieEntity>>> = flow {
        emit(Resource.loading())

        // Optionally emit cached data first
        // emit(Resource.success(daoQuery().firstOrNull())) // Example: emit current cache

        try {
            val response = movieDbService.getMoviesList(
                sortBy = sortBy,
                apiKey = CAConstants.API_KEY, // Using placeholder API_KEY
                page = page,
                includeAdult = false
            )
            val movieEntities = response.results?.map { it.toMovieEntity() } ?: emptyList()
            
            // For simplicity, we replace all movies of a certain kind.
            // A more advanced strategy might only clear non-favorites or use timestamps.
            // If page == 1, clear old data for this sort order (this is a very basic cache handling)
            // This part needs a more robust strategy for a real app (e.g. how to handle pagination and old data)
            // For now, we just insert, relying on OnConflictStrategy.REPLACE for MovieEntity (based on movieId)
            movieDao.insertAll(movieEntities)

            // Emit data from DAO after update
            emit(Resource.success(daoQuery().firstOrNull())) // Or use map to transform Flow<List<MovieEntity>> to Flow<Resource<List<MovieEntity>>>

        } catch (e: Exception) {
            // Emit error, potentially with cached data
             val cachedData = daoQuery().firstOrNull()
            emit(Resource.error("API Error: ${e.message}", cachedData))
        }
    }.flowOn(Dispatchers.IO)


    override fun getMovieDetails(movieId: Long): Flow<Resource<MovieEntity>> = flow {
        emit(Resource.loading())
        try {
            val movie = movieDao.getMovieByApiId(movieId)
            if (movie != null) {
                emit(Resource.success(movie)) // Emit cached first
            }

            // Placeholder: In a real app, you'd fetch detailed movie info from API
            // including reviews and videos if not already part of MovieEntity or if needing fresh data
            // For now, we assume the entity from list is sufficient or this method needs more work.
            // val apiMovieDetails = movieDbService.getMovieDetails(movieId, CAConstants.API_KEY) // Imaginary endpoint
            // val updatedEntity = apiMovieDetails.toMovieEntityWithDetails()
            // movieDao.insert(updatedEntity)
            // emit(Resource.success(movieDao.getMovieByApiId(movieId)))

            if (movie == null) { // If not in cache, this path is problematic without API call
                 emit(Resource.error("Movie not found in cache and remote fetch not fully implemented.", null))
            }

        } catch (e: Exception) {
            emit(Resource.error("Error fetching movie details: ${e.message}", movieDao.getMovieByApiId(movieId)))
        }
    }.flowOn(Dispatchers.IO)

    override fun getFavoriteMovies(): Flow<List<MovieEntity>> {
        return movieDao.getFavoriteMovies().flowOn(Dispatchers.IO)
    }

    override suspend fun toggleFavoriteStatus(movie: MovieEntity): Resource<Unit> {
        return try {
            val movieToUpdate = movieDao.getMovieByApiId(movie.movieId) ?: movie // Get from DB if possible to have latest state
            val updatedMovie = movieToUpdate.copy(isFavorite = !movieToUpdate.isFavorite)
            movieDao.update(updatedMovie)
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("Failed to update favorite status: ${e.message}")
        }
    }
    
    override suspend fun refreshMovies(sortOrder: String, page: Int): Resource<Unit> {
        // This is a simplified refresh. It just calls the internal fetch logic.
        // A real implementation might clear specific cache entries or handle errors differently.
        emitData(getMoviesInternal(sortOrder, page) {
            when(sortOrder) {
                SORT_BY_POPULARITY -> movieDao.getAllMoviesSortedByPopularity()
                SORT_BY_TOP_RATED -> movieDao.getAllMoviesSortedByVoteAverage()
                else -> movieDao.getAllMovies() // fallback
            }
        })
        return Resource.success(Unit) // The actual success/failure is emitted by the flow
    }

    // Helper to collect and emit from a flow within a suspend function (for refresh)
    private suspend fun <T> emitData(flow: Flow<T>) {
        flow.collect { /* Consume to trigger execution, actual data emitted by the flow itself */ }
    }

    override fun getMovieReviews(movieId: Long): Flow<Resource<List<ReviewResponseBean>>> = flow {
        emit(Resource.loading())
        try {
            val response = movieDbService.getMovieReviews(movieId, CAConstants.API_KEY) // Using existing API_KEY placeholder
            emit(Resource.success(response.results ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.error("Failed to fetch reviews: ${e.message}", null))
        }
    }.flowOn(Dispatchers.IO)

    override fun getMovieVideos(movieId: Long): Flow<Resource<List<VideoResponseBean>>> = flow {
        emit(Resource.loading())
        try {
            val response = movieDbService.getMovieVideos(movieId, CAConstants.API_KEY) // Using existing API_KEY placeholder
            emit(Resource.success(response.results ?: emptyList()))
        } catch (e: Exception) {
            emit(Resource.error("Failed to fetch videos: ${e.message}", null))
        }
    }.flowOn(Dispatchers.IO)
}

// Extension function to map API response to local entity
fun MoviesResponseBean.toMovieEntity(): MovieEntity {
    return MovieEntity(
        movieId = this.id,
        title = this.title,
        posterPath = this.posterPath, // Store only the path
        backdropPath = this.backdropPath, // Store only the path
        originalTitle = this.originalTitle,
        plot = this.overview,
        rating = this.voteAverage,
        releaseDate = this.releaseDate,
        genreIds = this.genreIds,
        isFavorite = false, // Default, will be updated by user action
        popularity = this.popularity,
        voteAverage = this.voteAverage,
        fetchedAt = System.currentTimeMillis()
    )
}
