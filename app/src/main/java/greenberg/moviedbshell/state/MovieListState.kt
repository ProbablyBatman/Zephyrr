package greenberg.moviedbshell.state

import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.state.base.BaseMovieListState

// TODO: maybe remove base class?
data class MovieListState(
    override val pageNumber: Int = 1,
    override val movieList: List<MovieItem> = emptyList(),
    override val shouldShowMaxPages: Boolean = false,
    override val isLoading: Boolean = true,
    override val error: Throwable? = null,
) : BaseMovieListState(pageNumber, movieList, shouldShowMaxPages, isLoading, error)
