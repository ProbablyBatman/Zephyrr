package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.MovieListState
import greenberg.moviedbshell.view.PopularMoviesFragment
import greenberg.moviedbshell.viewmodel.base.BaseMovieListViewModel
import io.reactivex.schedulers.Schedulers

class PopularMoviesViewModel
@AssistedInject constructor(
    @Assisted override var state: MovieListState,
    private val TMDBService: TMDBService,
    private val mapper: MovieListMapper
) : BaseMovieListViewModel<MovieListState>(state) {

    @AssistedFactory
    interface Factory : BaseMovieListViewModel.Factory {
        fun create(state: MovieListState): PopularMoviesViewModel
    }

    init {
        logStateChanges()
        fetchFirstPage()
    }

    override fun fetchMovies() {
        withState { state ->
            val totalPages = state.movieListResponse()?.totalPages
            if (totalPages != null && state.pageNumber >= totalPages) {
                state.copy(shouldShowMaxPages = true)
                return@withState
            }
            TMDBService
                .queryPopularMovies(state.pageNumber)
                .subscribeOn(Schedulers.io())
                .execute {
                    when (it) {
                        is Fail -> {
                            copy(
                                pageNumber = state.pageNumber,
                                movieListResponse = it,
                                movieList = state.movieList
                            )
                        }
                        is Success -> {
                            val pages = it()?.totalPages
                            copy(
                                pageNumber = state.pageNumber + 1,
                                movieListResponse = it,
                                movieList = state.movieList + mapper.mapToEntity(it()),
                                shouldShowMaxPages = pages != null && pages <= state.pageNumber
                            )
                        }
                        else -> copy(
                            movieListResponse = it
                        )
                    }
                }
                .disposeOnClear()
        }
    }

    fun fetchFirstPage() {
        withState { state ->
            TMDBService
                .queryPopularMovies(1)
                .subscribeOn(Schedulers.io())
                .execute {
                    when (it) {
                        is Fail -> {
                            copy(
                                pageNumber = 1,
                                movieListResponse = it,
                                movieList = state.movieList
                            )
                        }
                        is Success -> {
                            copy(
                                pageNumber = state.pageNumber + 1,
                                movieListResponse = it,
                                movieList = state.movieList + mapper.mapToEntity(it())
                            )
                        }
                        else -> copy()
                    }
                }
                .disposeOnClear()
        }
    }

    // TODO: Determine if this is still necessary
//    private fun evictCachedUrls() {
//        val iterator = httpClient.cache?.urls()
//        while (iterator?.hasNext() == true) {
//            val currentUrl = iterator.next()
//            if (currentUrl.contains(context.getString(R.string.tmdb_popular_url))) {
//                Timber.d("Removed $currentUrl from OkHttpCache")
//                iterator.remove()
//            }
//        }
//    }

    companion object : MvRxViewModelFactory<PopularMoviesViewModel, MovieListState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: MovieListState): PopularMoviesViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<PopularMoviesFragment>().viewModelFactory
            return fragment.create(state)
        }
    }
}