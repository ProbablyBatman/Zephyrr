package greenberg.moviedbshell.viewmodel.base

import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.state.base.BaseMovieListState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class BaseMovieListViewModel<T : BaseMovieListState>(
    open var state: T
) : ZephyrrMvRxViewModel<T>(state) {

    interface Factory

    abstract fun fetchMovies(dispatcher: CoroutineDispatcher = Dispatchers.IO)
}
