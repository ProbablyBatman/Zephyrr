package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.PopularMovieMapper
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.PopularMovieState
import greenberg.moviedbshell.view.PopularMoviesFragment
import io.reactivex.schedulers.Schedulers

class PopularMoviesViewModel
@AssistedInject constructor(
    @Assisted var state: PopularMovieState,
    private val TMDBService: TMDBService,
    private val mapper: PopularMovieMapper
) : ZephyrrMvRxViewModel<PopularMovieState>(state) {

    @AssistedInject.Factory
    interface Factory {
        fun create(state: PopularMovieState): PopularMoviesViewModel
    }

    init {
        logStateChanges()
        fetchPopularMovies()
    }

    // TODO: do I need this like this
//    init {
//        fetchPopularMovies()
//    }

    fun fetchPopularMovies() {
        withState { state ->
            TMDBService
                    .queryPopularMovies(state.pageNumber)
                    .subscribeOn(Schedulers.io())
                    .execute {
                        // If call fails, pass the same state through, but it's a copy with the Async
                        // where Async is of Fail type.
                        if (it is Fail) {
                            copy(
                                popularMovieResponse = it,
                                pageNumber = state.pageNumber,
                                popularMovies = state.popularMovies
                            )
                        } else {
                            copy(
                                popularMovieResponse = it,
                                pageNumber = state.pageNumber + 1,
                                popularMovies = state.popularMovies + mapper.mapToEntity(it())
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
                        // If call fails, pass the same state through, but it's a copy with the Async
                        // where Async is of Fail type.
                        if (it is Fail) {
                            copy(
                                    popularMovieResponse = it,
                                    pageNumber = 1,
                                    popularMovies = emptyList()
                            )
                        } else {
                            copy(
                                    popularMovieResponse = it,
                                    pageNumber = 2,
                                    popularMovies = mapper.mapToEntity(it())
                            )
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

    companion object : MvRxViewModelFactory<PopularMoviesViewModel, PopularMovieState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: PopularMovieState): PopularMoviesViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<PopularMoviesFragment>().popularMoviesViewModelFactory
            return fragment.create(state)
        }
    }
}