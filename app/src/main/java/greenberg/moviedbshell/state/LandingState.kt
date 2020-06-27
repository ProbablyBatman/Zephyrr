package greenberg.moviedbshell.state

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.popularmoviesmodels.PopularMovieResponse
import greenberg.moviedbshell.models.recentlyreleasedmodels.RecentlyReleasedResponse
import greenberg.moviedbshell.models.soontmmodels.SoonTMResponse
import greenberg.moviedbshell.models.ui.LandingItem

data class LandingState(
    val recentlyReleasedResponse: Async<RecentlyReleasedResponse> = Uninitialized,
    val popularMovieResponse: Async<PopularMovieResponse> = Uninitialized,
    val soonTMResponse: Async<SoonTMResponse> = Uninitialized,
    val landingItem: LandingItem? = null
//    val recentlyReleasedMovies: List<MovieItem> = emptyList(),
//    val popularMovies: List<MovieItem> = emptyList(),
//    val soonTMMovies: List<MovieItem> = emptyList()
) : MvRxState

// These two functions are part of inherently concating all the responses here.
// Should probably look into some other method of this
fun LandingState.shouldShowLoading(): Boolean =
    when {
        this.recentlyReleasedResponse == Uninitialized ||
            this.popularMovieResponse == Uninitialized ||
            this.soonTMResponse == Uninitialized -> true
        !this.recentlyReleasedResponse.complete ||
            !this.popularMovieResponse.complete ||
            !this.soonTMResponse.complete -> true
        this.recentlyReleasedResponse.complete &&
            this.popularMovieResponse.complete &&
            this.soonTMResponse.complete -> false
        else -> false
    }

fun LandingState.shouldShowError(): Boolean =
    when {
        this.recentlyReleasedResponse is Fail ||
            this.popularMovieResponse is Fail ||
            this.soonTMResponse is Fail -> true
        else -> false
    }