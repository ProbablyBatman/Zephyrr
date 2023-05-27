package greenberg.moviedbshell.state

import greenberg.moviedbshell.models.movielistmodels.MovieListResponse
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.state.base.BaseMovieListState

data class MovieListState(
    override val pageNumber: Int = 1,
    override val movieListResponse: Async<MovieListResponse> = Uninitialized,
    override val movieList: List<MovieItem> = emptyList(),
    override val shouldShowMaxPages: Boolean = false
) : BaseMovieListState(pageNumber, movieListResponse, movieList, shouldShowMaxPages)
