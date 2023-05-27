package greenberg.moviedbshell.state

import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.TvItem

sealed class LandingPageUiState {

    object Loading : LandingPageUiState()

    data class Success(
        val isLoading: Boolean,
        val popularMovieList: List<MovieItem>,
        val recentlyReleasedMovieList: List<MovieItem>,
        val soonTMMovieList: List<MovieItem>,
        val popularTVList: List<TvItem>,
        val topRatedTVList: List<TvItem>,
    ) : LandingPageUiState()

    data class Failure(
        val error: Throwable? = null,
    ) : LandingPageUiState()
}