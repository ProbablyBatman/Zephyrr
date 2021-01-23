package greenberg.moviedbshell.state

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.movielistmodels.MovieListResponse
import greenberg.moviedbshell.models.tvlistmodels.TvListResponse
import greenberg.moviedbshell.models.ui.LandingItem
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.TvItem

data class LandingState(
    val recentlyReleasedResponse: Async<MovieListResponse> = Uninitialized,
    val popularMovieResponse: Async<MovieListResponse> = Uninitialized,
    val soonTMResponse: Async<MovieListResponse> = Uninitialized,
    val popularTvResponse: Async<TvListResponse> = Uninitialized,
    val topRatedTvResponse: Async<TvListResponse> = Uninitialized,
    val recentlyReleasedItems: List<MovieItem> = emptyList(),
    val popularMovieItems: List<MovieItem> = emptyList(),
    val soonTMItems: List<MovieItem> = emptyList(),
    val popularTvItems: List<TvItem> = emptyList(),
    val topRatedTvItems: List<TvItem> = emptyList()
) : MvRxState
