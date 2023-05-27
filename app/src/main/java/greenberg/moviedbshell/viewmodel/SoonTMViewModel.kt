package greenberg.moviedbshell.viewmodel

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.state.MovieListState
import greenberg.moviedbshell.view.SoonTMFragment
import greenberg.moviedbshell.viewmodel.base.BaseMovieListViewModel
import kotlinx.coroutines.CoroutineDispatcher

class SoonTMViewModel
@AssistedInject constructor(
    @Assisted override var state: MovieListState,
    private val tmdbRepository: TmdbRepository,
    private val mapper: MovieListMapper
) : BaseMovieListViewModel<MovieListState>(state) {

    @AssistedFactory
    interface Factory : BaseMovieListViewModel.Factory {
        fun create(state: MovieListState): SoonTMViewModel
    }

    init {
        fetchMovies()
    }

    override fun fetchMovies(dispatcher: CoroutineDispatcher) {
//        withState { state ->
//            suspend { tmdbRepository.fetchSoonTM(state.pageNumber) }
//                .execute(dispatcher) {
//                    val totalPages = it()?.totalPages
//                    when (it) {
//                        is Fail -> {
//                            copy(
//                                pageNumber = state.pageNumber,
//                                movieListResponse = it,
//                                movieList = state.movieList,
//                                shouldShowMaxPages = totalPages != null && state.pageNumber >= totalPages
//                            )
//                        }
//                        is Success -> {
//                            copy(
//                                pageNumber = state.pageNumber + 1,
//                                movieListResponse = it,
//                                movieList = state.movieList + mapper.mapToEntity(it()),
//                                shouldShowMaxPages = totalPages != null && state.pageNumber >= totalPages
//                            )
//                        }
//                        else -> copy(
//                            movieListResponse = it
//                        )
//                    }
//                }
//        }
    }

//    companion object : MavericksViewModelFactory<SoonTMViewModel, MovieListState> {
//        override fun create(viewModelContext: ViewModelContext, state: MovieListState): SoonTMViewModel {
//            val fragment = (viewModelContext as FragmentViewModelContext).fragment<SoonTMFragment>().viewModelFactory
//            return fragment.create(state)
//        }
//    }
}
