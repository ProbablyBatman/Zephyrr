package greenberg.moviedbshell.viewmodel.base

import androidx.lifecycle.ViewModel
import greenberg.moviedbshell.state.base.BaseMovieListState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class BaseMovieListViewModel<T : BaseMovieListState>(
    open var state: T
) : ViewModel() {

    interface Factory

    abstract fun fetchMovies(dispatcher: CoroutineDispatcher = Dispatchers.IO)
}
