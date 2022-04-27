package greenberg.moviedbshell.state

import greenberg.moviedbshell.models.ui.MovieItem

sealed class MovieListUiState {

    object Loading : MovieListUiState()

    data class Success(
        val pageNumber: Int,
        val isLoading: Boolean,
        val movieList: List<MovieItem>,
        val shouldShowMaxPages: Boolean,
    ) : MovieListUiState()

    data class Failure(
        val error: Throwable? = null
    ) : MovieListUiState()
}
