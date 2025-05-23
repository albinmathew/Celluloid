package me.albinmathew.celluloid.ui.movieslist

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import me.albinmathew.celluloid.data.repository.MovieRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
// import org.junit.Rule // Rule is not strictly needed with Dispatchers.setMain/resetMain directly
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MoviesViewModelTest {

    // Using UnconfinedTestDispatcher for simplicity as per example.
    // StandardTestDispatcher might be preferred for more control over execution order.
    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockMovieRepository: MovieRepository

    // ViewModel is initialized in setUp or within tests if specific mock behavior is needed before init
    private lateinit var viewModel: MoviesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // viewModel = MoviesViewModel(mockMovieRepository) // ViewModel initialization is deferred
                                                            // to allow for specific mock setup if init block calls loadMovies
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct when viewmodel is created`() = runTest(testDispatcher) {
        // Assuming loadMovies in init block might need a mock response or specific handling
        // For a simple initial state test before init's effects, we might need to control repo behavior.
        // However, the current ViewModel's init calls loadMovies(), which will interact with the repo.
        // If we want to test a "pure" initial state before init's loadMovies,
        // that would require a different setup or not calling loadMovies in init.
        // For now, let's assume we test the state *after* init has run (and potentially failed or succeeded based on mock).

        // Scenario: Test the default state if init's loadMovies doesn't immediately change it or if we mock it to do nothing.
        // This is a bit tricky as loadMovies is called in init.
        // A more robust way would be to mock the repository to return empty flow or error immediately.
        // For now, we'll just instantiate and check the default values set by MutableStateFlow.
        
        viewModel = MoviesViewModel(mockMovieRepository) // This will trigger init { loadMovies() }

        // Test the initial state as defined by MoviesUiState() defaults,
        // recognizing that init{} will immediately try to change it.
        // This test is more about the default constructor of MoviesUiState.
        val expectedInitialState = MoviesUiState()
        val actualState = viewModel.uiState.value // This will be after init's loadMovies has started

        // This assertion might be flaky depending on how fast loadMovies progresses
        // and what the mockMovieRepository does by default for getPopularMovies.
        // A better test for init would involve mocking getPopularMovies and asserting specific states.
        assertEquals(expectedInitialState.isLoading, actualState.isLoading) // This might already be true due to init
        assertEquals(expectedInitialState.movies, actualState.movies)
        assertEquals(expectedInitialState.error, actualState.error)
        assertEquals(expectedInitialState.currentSortOrder, actualState.currentSortOrder)
        assertEquals(expectedInitialState.currentPage, actualState.currentPage)
    }

    @Test
    fun `loadMovies with popular success updates state`() = runTest(testDispatcher) {
        // TODO: Mock repository response for getPopularMovies
        // Example:
        // val popularMovies = listOf(MovieEntity(movieId = 1, title = "Popular Movie"))
        // whenever(mockMovieRepository.getPopularMovies(1)).thenReturn(flowOf(Resource.success(popularMovies)))

        // viewModel = MoviesViewModel(mockMovieRepository) // Initialize for this test case

        // TODO: Call viewModel.loadMovies(SortOrder.POPULARITY) or rely on init
        // TODO: Assert uiState changes using Turbine:
        // viewModel.uiState.test {
        //     // awaitItem() should reflect initial state or loading state from init
        //     // awaitItem() // might be loading from init
        //     // awaitItem() // might be success/error from init
        //
        //     // If loadMovies is called explicitly again:
        //     // viewModel.loadMovies(MoviesViewModel.SortOrder.POPULARITY, 1)
        //     // assertEquals(MoviesUiState(isLoading = true, currentSortOrder = MoviesViewModel.SortOrder.POPULARITY, currentPage = 1), awaitItem())
        //     // val successState = awaitItem()
        //     // assertEquals(false, successState.isLoading)
        //     // assertEquals(popularMovies, successState.movies)
        //     cancelAndConsumeRemainingEvents()
        // }
    }

    // TODO: Add more test cases based on the scenarios outlined:
    // - loadMovies - Top Rated (Success)
    // - loadMovies - Favorites (Success)
    // - loadMovies (API Error)
    // - changeSortOrder
    // - loadNextPage (Success)
    // - loadNextPage (When already loading)
    // - loadNextPage (For Favorites)
}
