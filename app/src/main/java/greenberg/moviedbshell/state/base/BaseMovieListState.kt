package greenberg.moviedbshell.state.base

import greenberg.moviedbshell.models.movielistmodels.MovieListResponse
import greenberg.moviedbshell.models.ui.MovieItem

abstract class BaseMovieListState(
    open val pageNumber: Int,
    open val movieListResponse: Async<MovieListResponse>,
    open val movieList: List<MovieItem>,
    open val shouldShowMaxPages: Boolean
)
