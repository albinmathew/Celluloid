package me.albinmathew.celluloid.ui.moviedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.albinmathew.celluloid.api.response.ReviewResponseBean
import me.albinmathew.celluloid.api.response.VideoResponseBean
import me.albinmathew.celluloid.data.local.entity.MovieEntity
import me.albinmathew.celluloid.data.repository.MovieRepository
import me.albinmathew.celluloid.data.util.Resource
import javax.inject.Inject

data class MovieDetailsUiState(
    val isLoading: Boolean = false,
    val movie: MovieEntity? = null,
    val reviews: List<ReviewResponseBean> = emptyList(),
    val videos: List<VideoResponseBean> = emptyList(),
    val error: String? = null,
    val isFavorite: Boolean = false
)

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    private val movieId: Long? = savedStateHandle.get<Long>("movieId")

    init {
        movieId?.let {
            if (it > 0) { // Basic validation
                loadMovieDetails(it)
            } else {
                _uiState.update { state -> state.copy(error = "Invalid Movie ID", isLoading = false) }
            }
        } ?: _uiState.update { state -> state.copy(error = "Movie ID not found", isLoading = false) }
    }

    fun loadMovieDetails(id: Long) {
        viewModelScope.launch {
            repository.getMovieDetails(id).collect { resource ->
                when (resource.status) {
                    Resource.Status.LOADING -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    Resource.Status.SUCCESS -> {
                        val movieEntity = resource.data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                movie = movieEntity,
                                isFavorite = movieEntity?.isFavorite ?: false,
                                error = if (movieEntity == null) "Movie not found" else null
                            )
                        }
                        if (movieEntity != null) {
                            loadMovieReviews(id)
                            loadMovieVideos(id)
                        }
                    }
                    Resource.Status.ERROR -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = resource.message)
                        }
                    }
                }
            }
        }
    }

    private fun loadMovieReviews(movieId: Long) {
        viewModelScope.launch {
            repository.getMovieReviews(movieId).collect { resource ->
                if (resource.status == Resource.Status.SUCCESS) {
                    _uiState.update { it.copy(reviews = resource.data ?: emptyList()) }
                } else if (resource.status == Resource.Status.ERROR) {
                    // Optionally handle review loading errors separately on UI
                    // For now, just updating state, main error is for movie details
                     _uiState.update { it.copy(error = it.error ?: resource.message) } // Append if movie error already exists
                }
            }
        }
    }

    private fun loadMovieVideos(movieId: Long) {
        viewModelScope.launch {
            repository.getMovieVideos(movieId).collect { resource ->
                if (resource.status == Resource.Status.SUCCESS) {
                    _uiState.update { it.copy(videos = resource.data ?: emptyList()) }
                } else if (resource.status == Resource.Status.ERROR) {
                     _uiState.update { it.copy(error = it.error ?: resource.message) }
                }
            }
        }
    }

    fun toggleFavorite() {
        val currentMovie = _uiState.value.movie ?: return
        viewModelScope.launch {
            // Create a copy with toggled favorite status for immediate UI update (optimistic)
            val updatedMovieOptimistic = currentMovie.copy(isFavorite = !currentMovie.isFavorite)
            _uiState.update { it.copy(movie = updatedMovieOptimistic, isFavorite = updatedMovieOptimistic.isFavorite) }

            val result = repository.toggleFavoriteStatus(currentMovie) // currentMovie still has the original isFavorite state
            if (result.status == Resource.Status.ERROR) {
                // Revert optimistic update on error
                _uiState.update {
                    it.copy(
                        movie = currentMovie, // Revert to original movie state
                        isFavorite = currentMovie.isFavorite,
                        error = result.message ?: "Failed to toggle favorite"
                    )
                }
            } else {
                 // Optionally re-fetch to confirm, or trust the toggle was successful.
                 // For now, we trust the optimistic update if no error.
                 // If repository.toggleFavoriteStatus itself updated the DB and returned the updated entity,
                 // we could use that. Our current toggleFavoriteStatus returns Resource<Unit>.
                 // To be absolutely sure, one might refetch:
                 // loadMovieDetails(currentMovie.movieId)
            }
        }
    }
}
