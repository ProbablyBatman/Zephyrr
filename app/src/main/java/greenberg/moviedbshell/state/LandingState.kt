package greenberg.moviedbshell.state

import greenberg.moviedbshell.base.ZephyrrResponse
import greenberg.moviedbshell.models.movielistmodels.MovieListResponse
import greenberg.moviedbshell.models.tvlistmodels.TvListResponse
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.TvItem
import greenberg.moviedbshell.state.base.ZephyrrState

//data class LandingState(
//    val recentlyReleasedResponse: Async<MovieListResponse> = Uninitialized,
//    val popularMovieResponse: Async<MovieListResponse> = Uninitialized,
//    val soonTMResponse: Async<MovieListResponse> = Uninitialized,
//    val popularTvResponse: Async<TvListResponse> = Uninitialized,
//    val topRatedTvResponse: Async<TvListResponse> = Uninitialized,
//    val recentlyReleasedItems: List<MovieItem> = emptyList(),
//    val popularMovieItems: List<MovieItem> = emptyList(),
//    val soonTMItems: List<MovieItem> = emptyList(),
//    val popularTvItems: List<TvItem> = emptyList(),
//    val topRatedTvItems: List<TvItem> = emptyList()
//) : ZephyrrState

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
