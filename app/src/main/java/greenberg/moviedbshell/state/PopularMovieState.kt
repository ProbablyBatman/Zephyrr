package greenberg.moviedbshell.state

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.popularmoviesmodels.PopularMovieResponse
import greenberg.moviedbshell.models.ui.MovieItem

data class PopularMovieState(
    val pageNumber: Int = 1,
    val popularMovieResponse: Async<PopularMovieResponse> = Uninitialized,
    val popularMovies: List<MovieItem> = emptyList()
) : MvRxState