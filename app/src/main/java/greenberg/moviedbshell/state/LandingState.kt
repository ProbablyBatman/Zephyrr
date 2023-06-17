package greenberg.moviedbshell.state

import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.TvItem

// TODO: file rename
data class MovieLandingState(
    val response: List<MovieItem> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = true,
)

data class TvLandingState(
    val response: List<TvItem> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = true,
)
