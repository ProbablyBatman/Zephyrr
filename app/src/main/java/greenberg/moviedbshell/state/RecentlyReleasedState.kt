package greenberg.moviedbshell.state

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.recentlyreleasedmodels.RecentlyReleasedResponse
import greenberg.moviedbshell.models.ui.MovieItem

data class RecentlyReleasedState(
    val pageNumber: Int = 1,
    val recentlyReleasedResponse: Async<RecentlyReleasedResponse> = Uninitialized,
    val recentlyReleasedMovies: List<MovieItem> = emptyList()
) : MvRxState