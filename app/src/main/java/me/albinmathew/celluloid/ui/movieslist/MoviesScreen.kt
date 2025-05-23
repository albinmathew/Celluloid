package me.albinmathew.celluloid.ui.movieslist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.albinmathew.celluloid.R // Assuming R class is available
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun MoviesScreen(
    viewModel: MoviesViewModel = hiltViewModel(),
    onMovieClick: (movieId: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showSortMenu by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading && uiState.movies.isEmpty(), // Show pull refresh only on initial load
        onRefresh = { viewModel.refresh() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Filled.Sort, contentDescription = "Sort Movies")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        MoviesViewModel.SortOrder.values().forEach { sortOrder ->
                            DropdownMenuItem(
                                text = { Text(sortOrder.name.replace("_", " ").replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                }) },
                                onClick = {
                                    viewModel.changeSortOrder(sortOrder)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Apply padding from Scaffold
            .pullRefresh(pullRefreshState)
        ) {
            if (uiState.isLoading && uiState.movies.isEmpty() && uiState.error == null) {
                // Full screen loader for initial load only if no error
                 if (!pullRefreshState.refreshing) { // Don't show if pullRefresh indicator is already showing
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else if (uiState.error != null) {
                Text(
                    text = "Error: ${uiState.error}",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            } else if (uiState.movies.isEmpty() && !uiState.isLoading) {
                 Text(
                    text = "No movies found.", // Or specific message for favorites if empty
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else {
                val lazyGridState = rememberLazyGridState()
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    state = lazyGridState,
                    contentPadding = PaddingValues(4.dp), // Inner padding for grid items
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.movies.size) { index ->
                        val movie = uiState.movies[index]
                        MovieItem(movie = movie, onMovieClick = onMovieClick)
                    }

                    if (uiState.isLoading && uiState.movies.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

                LaunchedEffect(lazyGridState, uiState.isLoading, uiState.movies.size) {
                    snapshotFlow { lazyGridState.layoutInfo.visibleItemsInfo }
                        .collect { visibleItems ->
                            if (visibleItems.isNotEmpty() && !uiState.isLoading) {
                                val lastVisibleItem = visibleItems.last()
                                if (lastVisibleItem.index >= uiState.movies.size - 5 && uiState.movies.isNotEmpty()) {
                                    viewModel.loadNextPage()
                                }
                            }
                        }
                }
            }
            PullRefreshIndicator(
                refreshing = uiState.isLoading && uiState.movies.isEmpty(), // Match pullRefreshState's refreshing condition
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}
