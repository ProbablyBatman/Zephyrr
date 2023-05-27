package greenberg.moviedbshell.viewmodel

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.mappers.TvListMapper
import greenberg.moviedbshell.state.LandingState
import greenberg.moviedbshell.view.LandingFragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LandingViewModel
@AssistedInject constructor(
    @Assisted var initialState: LandingState,
    private val tmdbRepository: TmdbRepository,
    private val movieListMapper: MovieListMapper,
    private val tvListMapper: TvListMapper
) : ZephyrrMvRxViewModel<LandingState>(initialState) {

    @AssistedFactory
    interface Factory {
        fun create(initialState: LandingState): LandingViewModel
    }

    init {
        fetchLandingResults()
    }

    private fun fetchLandingResults() {
        getRecentlyReleased()
        getPopularMovies()
        getSoonTM()
        getPopularTv()
        getTopRatedTv()
    }

    private fun getRecentlyReleased(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        suspend { tmdbRepository.fetchRecentlyReleased(1) }
            .execute(dispatcher) {
                copy(
                    recentlyReleasedResponse = it,
                    recentlyReleasedItems = movieListMapper.mapToEntity(it())
                )
            }
    }

    private fun getPopularMovies(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        suspend { tmdbRepository.fetchPopularMovies(1) }
            .execute(dispatcher) {
                copy(
                    popularMovieResponse = it,
                    popularMovieItems = movieListMapper.mapToEntity(it())
                )
            }
    }

    private fun getSoonTM(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        suspend { tmdbRepository.fetchSoonTM(1) }
            .execute(dispatcher) {
                copy(
                    soonTMResponse = it,
                    soonTMItems = movieListMapper.mapToEntity(it())
                )
            }
    }

    private fun getPopularTv(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        suspend { tmdbRepository.fetchPopularTv(1) }
            .execute(dispatcher) {
                copy(
                    popularTvResponse = it,
                    popularTvItems = tvListMapper.mapToEntity(it())
                )
            }
    }

    private fun getTopRatedTv(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        suspend { tmdbRepository.fetchTopRatedTv(1) }
            .execute(dispatcher) {
                copy(
                    topRatedTvResponse = it,
                    topRatedTvItems = tvListMapper.mapToEntity(it())
                )
            }
    }

    fun retryRecentlyReleased() {
        viewModelScope.launch {
            getRecentlyReleased()
        }
    }

    fun retryPopularMovies() {
        viewModelScope.launch {
            getPopularMovies()
        }
    }

    fun retrySoonTM() {
        viewModelScope.launch {
            getSoonTM()
        }
    }

    fun retryPopularTv() {
        viewModelScope.launch {
            getPopularTv()
        }
    }

    fun retryTopRatedTv() {
        viewModelScope.launch {
            getTopRatedTv()
        }
    }

    companion object : MavericksViewModelFactory<LandingViewModel, LandingState> {
        override fun create(viewModelContext: ViewModelContext, state: LandingState): LandingViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<LandingFragment>().landingViewModelFactory
            return fragment.create(state)
        }
    }
}
