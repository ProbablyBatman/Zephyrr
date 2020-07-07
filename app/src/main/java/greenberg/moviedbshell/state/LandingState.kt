package greenberg.moviedbshell.state

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.popularmoviesmodels.PopularMovieResponse
import greenberg.moviedbshell.models.recentlyreleasedmodels.RecentlyReleasedResponse
import greenberg.moviedbshell.models.soontmmodels.SoonTMResponse
import greenberg.moviedbshell.models.ui.LandingItem

data class LandingState(
    val landingItem: Async<LandingItem> = Uninitialized
) : MvRxState

// These two functions are part of inherently concating all the responses here.
// Should probably look into some other method of this
fun LandingState.shouldShowLoading(): Boolean =
    when (this.landingItem) {
        Uninitialized -> true
        is Loading -> true
        is Success -> false
        else -> false
    }

fun LandingState.shouldShowError(): Boolean =
    when (this.landingItem) {
        is Fail -> true
        else -> false
    }