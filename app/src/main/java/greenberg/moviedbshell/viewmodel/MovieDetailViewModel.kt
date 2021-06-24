package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.TmdbRepository
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.state.MovieDetailState
import greenberg.moviedbshell.view.MovieDetailFragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class MovieDetailViewModel
@AssistedInject constructor(
    @Assisted var state: MovieDetailState,
    private val tmdbRepository: TmdbRepository
) : ZephyrrMvRxViewModel<MovieDetailState>(state) {

    @AssistedFactory
    interface Factory {
        fun create(state: MovieDetailState): MovieDetailViewModel
    }

    init {
        fetchMovieDetail()
    }

    fun fetchMovieDetail(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        withState { state ->
            // Swallows requests if there's already one loading
            if (state.movieDetailResponse is Loading) return@withState
            suspend { tmdbRepository.fetchMovieDetail(viewModelScope, state.movieId) }
                .execute(dispatcher) {
                    copy(
                        movieId = state.movieId,
                        movieDetailItem = it(),
                        movieDetailResponse = it
                    )
                }
        }
    }

    companion object : MavericksViewModelFactory<MovieDetailViewModel, MovieDetailState> {
        override fun create(viewModelContext: ViewModelContext, state: MovieDetailState): MovieDetailViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<MovieDetailFragment>().movieDetailViewModelFactory
            return fragment.create(state)
        }
    }
}