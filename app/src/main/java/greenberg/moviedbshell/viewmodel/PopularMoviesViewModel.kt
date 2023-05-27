package greenberg.moviedbshell.viewmodel

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.state.MovieListState
import greenberg.moviedbshell.view.PopularMoviesFragment
import greenberg.moviedbshell.viewmodel.base.BaseMovieListViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PopularMoviesViewModel
@AssistedInject constructor(
    @Assisted override var state: MovieListState,
    private val tmdbRepository: TmdbRepository,
    private val mapper: MovieListMapper
) : BaseMovieListViewModel<MovieListState>(state) {

    @AssistedFactory
    interface Factory : BaseMovieListViewModel.Factory {
        fun create(state: MovieListState): PopularMoviesViewModel
    }

    init {
        fetchFirstPage()
    }

    override fun fetchMovies(dispatcher: CoroutineDispatcher) {
        withState { state ->
            suspend { tmdbRepository.fetchPopularMovies(state.pageNumber) }
                .execute(dispatcher) {
                    val totalPages = it()?.totalPages
                    when (it) {
                        is Fail -> {
                            copy(
                                pageNumber = state.pageNumber,
                                movieListResponse = it,
                                movieList = state.movieList,
                                // TODO: move this to the view like in search?
                                shouldShowMaxPages = totalPages != null && state.pageNumber >= totalPages
                            )
                        }
                        is Success -> {
                            copy(
                                pageNumber = state.pageNumber + 1,
                                movieListResponse = it,
                                movieList = state.movieList + mapper.mapToEntity(it()),
                                shouldShowMaxPages = totalPages != null && state.pageNumber >= totalPages
                            )
                        }
                        else -> copy(
                            movieListResponse = it
                        )
                    }
                }
        }
    }

    private fun fetchFirstPage(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        withState { state ->
            suspend { tmdbRepository.fetchPopularMovies(1) }
                .execute(dispatcher) {
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

    companion object : MavericksViewModelFactory<PopularMoviesViewModel, MovieListState> {
        override fun create(viewModelContext: ViewModelContext, state: MovieListState): PopularMoviesViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<PopularMoviesFragment>().viewModelFactory
            return fragment.create(state)
        }
    }
}
