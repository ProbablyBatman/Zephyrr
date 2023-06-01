package greenberg.moviedbshell.viewmodel.base

import androidx.lifecycle.ViewModel
import greenberg.moviedbshell.state.base.BaseMovieListState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class BaseMovieListViewModel<T : BaseMovieListState>(
    protected open val movieId: Int,
    protected open val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    interface Factory

    abstract fun fetchMovies()
}
