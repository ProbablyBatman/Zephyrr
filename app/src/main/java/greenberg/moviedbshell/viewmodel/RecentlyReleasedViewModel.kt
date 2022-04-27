package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.state.MovieListState
import greenberg.moviedbshell.view.RecentlyReleasedFragment
import greenberg.moviedbshell.viewmodel.base.BaseMovieListViewModel
import kotlinx.coroutines.CoroutineDispatcher

class RecentlyReleasedViewModel
@AssistedInject constructor(
    @Assisted override var state: MovieListState,
    private val tmdbRepository: TmdbRepository,
    private val mapper: MovieListMapper
) : BaseMovieListViewModel<MovieListState>(state) {

    @AssistedFactory
    interface Factory : BaseMovieListViewModel.Factory {
        fun create(state: MovieListState): RecentlyReleasedViewModel
    }

    init {
        fetchMovies()
    }

    override fun fetchMovies(dispatcher: CoroutineDispatcher) {
        withState { state ->
            suspend { tmdbRepository.fetchRecentlyReleased(state.pageNumber) }
                .execute(dispatcher) {
                    val totalPages = it()?.totalPages
                    when (it) {
                        is Fail -> {
                            copy(
                                pageNumber = state.pageNumber,
                                movieListResponse = it,
                                movieList = state.movieList,
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

    companion object : MavericksViewModelFactory<RecentlyReleasedViewModel, MovieListState> {
        override fun create(viewModelContext: ViewModelContext, state: MovieListState): RecentlyReleasedViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<RecentlyReleasedFragment>().viewModelFactory
            return fragment.create(state)
        }
    }
}
