package greenberg.moviedbshell.viewmodel.base

import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.state.base.BaseMovieListState

abstract class BaseMovieListViewModel<T : BaseMovieListState>(
    open var state: T
) : ZephyrrMvRxViewModel<T>(state) {

    interface Factory

    abstract fun fetchMovies()
}
