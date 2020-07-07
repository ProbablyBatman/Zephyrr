package greenberg.moviedbshell.state

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.soontmmodels.SoonTMResponse
import greenberg.moviedbshell.models.ui.MovieItem

data class SoonTMState(
    val pageNumber: Int = 1,
    val soonTMResponse: Async<SoonTMResponse> = Uninitialized,
    val soonTMMovies: List<MovieItem> = emptyList()
) : MvRxState