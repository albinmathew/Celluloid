package me.albinmathew.celluloid.ui.movieslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.albinmathew.celluloid.data.local.entity.MovieEntity
import me.albinmathew.celluloid.data.repository.MovieRepository
import me.albinmathew.celluloid.data.util.Resource
import javax.inject.Inject

enum class SortOrder { POPULARITY, TOP_RATED, FAVORITES }

data class MoviesUiState(
    val isLoading: Boolean = false,
    val movies: List<MovieEntity> = emptyList(),
    val error: String? = null,
    val currentSortOrder: SortOrder = SortOrder.POPULARITY,
    val currentPage: Int = 1
)

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies(sortOrder: SortOrder = uiState.value.currentSortOrder, page: Int = 1) {
        // Prevent multiple loads if already loading, especially for pagination
        if (page > 1 && _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, currentPage = page, currentSortOrder = sortOrder) }

            val movieFlow: Flow<Resource<List<MovieEntity>>> = when (sortOrder) {
                SortOrder.POPULARITY -> repository.getPopularMovies(page)
                SortOrder.TOP_RATED -> repository.getTopRatedMovies(page)
                SortOrder.FAVORITES -> { // Favorites are loaded differently, not paginated from API
                    if (page == 1) { // Only load favorites on first page request for this sort order
                        repository.getFavoriteMovies().map { Resource.success(it) }
                    } else {
                        flowOf(Resource.success(emptyList())) // No pagination for favorites from API
                    }
                }
            }

            movieFlow.collect { resource ->
                when (resource.status) {
                    Resource.Status.LOADING -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    Resource.Status.SUCCESS -> {
                        _uiState.update { currentState ->
                            val newMovies = resource.data ?: emptyList()
                            val allMovies = if (page > 1 && sortOrder != SortOrder.FAVORITES) {
                                currentState.movies + newMovies // Append for pagination
                            } else {
                                newMovies // Replace for first page or favorites
                            }
                            currentState.copy(
                                isLoading = false,
                                movies = allMovies.distinctBy { it.movieId }, // Ensure unique movies
                                error = null
                            )
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

    fun changeSortOrder(newSortOrder: SortOrder) {
        if (newSortOrder != _uiState.value.currentSortOrder) {
            loadMovies(sortOrder = newSortOrder, page = 1)
        }
    }

    fun loadNextPage() {
        if (!_uiState.value.isLoading) { // Only load if not already loading
            val nextPage = _uiState.value.currentPage + 1
            // Favorites are not paginated from remote, so don't attempt to load next page for favorites
            if (_uiState.value.currentSortOrder != SortOrder.FAVORITES) {
                 loadMovies(sortOrder = _uiState.value.currentSortOrder, page = nextPage)
            }
        }
    }
}
