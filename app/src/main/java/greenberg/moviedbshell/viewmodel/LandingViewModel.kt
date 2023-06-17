package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrResponse
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.mappers.TvListMapper
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.state.MovieLandingState
import greenberg.moviedbshell.state.TvLandingState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class LandingViewModel
@AssistedInject constructor(
    @Assisted private val dispatcher: CoroutineDispatcher,
    private val tmdbRepository: TmdbRepository,
    private val movieListMapper: MovieListMapper,
    private val tvListMapper: TvListMapper,
) : ViewModel() {

    private val _popularMoviesLandingState = MutableStateFlow(MovieLandingState())
    val popularMoviesLandingState: StateFlow<MovieLandingState> = _popularMoviesLandingState.asStateFlow()

    private val _recentlyReleasedLandingState = MutableStateFlow(MovieLandingState())
    val recentlyReleasedLandingState: StateFlow<MovieLandingState> = _recentlyReleasedLandingState.asStateFlow()

    private val _soonTMLandingState = MutableStateFlow(MovieLandingState())
    val soonTMMoviesLandingState: StateFlow<MovieLandingState> = _soonTMLandingState.asStateFlow()

    private val _popularTvLandingState = MutableStateFlow(TvLandingState())
    val popularTvLandingState: StateFlow<TvLandingState> = _popularTvLandingState.asStateFlow()

    private val _topRatedTvLandingState = MutableStateFlow(TvLandingState())
    val topRatedTvLandingState: StateFlow<TvLandingState> = _topRatedTvLandingState.asStateFlow()

    // TODO: this is unnecessary I think
    @AssistedFactory
    interface Factory {
        fun create(dispatcher: CoroutineDispatcher): LandingViewModel
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

    private fun getRecentlyReleased() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching getRecentlyReleased")
            when (val response = tmdbRepository.fetchRecentlyReleased(1)) {
                is ZephyrrResponse.Success -> {
                    Timber.d("getRecentlyReleased success:$response")
                    _recentlyReleasedLandingState.emit(
                        _recentlyReleasedLandingState.value.copy(
                            response = movieListMapper.mapToEntity(response.value),
                            isLoading = false,
                            error = null,
                        ),
                    )
                }
                is ZephyrrResponse.Failure -> {
                    Timber.d("getRecentlyReleased failure:$response")
                    // TODO: do I copy the existing response if the new call fails?
                    // Retryable errors?
                    _recentlyReleasedLandingState.emit(
                        _recentlyReleasedLandingState.value.copy(
                            error = response.throwable,
                            isLoading = false,
                        ),
                    )
                }
            }
        }
    }

    private fun getPopularMovies() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching getPopularMovies")
            when (val response = tmdbRepository.fetchPopularMovies(1)) {
                is ZephyrrResponse.Success -> {
                    Timber.d("getPopularMovies success:$response")
                    _popularMoviesLandingState.emit(
                        _popularMoviesLandingState.value.copy(
                            response = movieListMapper.mapToEntity(response.value),
                            isLoading = false,
                            error = null,
                        ),
                    )
                }
                is ZephyrrResponse.Failure -> {
                    Timber.d("getPopularMovies failure:$response")
                    // TODO: do I copy the existing response if the new call fails?
                    // Retryable errors?
                    _popularMoviesLandingState.emit(
                        _popularMoviesLandingState.value.copy(
                            error = response.throwable,
                            isLoading = false,
                        ),
                    )
                }
            }
        }
    }

    private fun getSoonTM() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching getSoonTM")
            when (val response = tmdbRepository.fetchSoonTM(1)) {
                is ZephyrrResponse.Success -> {
                    Timber.d("getSoonTM success:$response")
                    _soonTMLandingState.emit(
                        _soonTMLandingState.value.copy(
                            response = movieListMapper.mapToEntity(response.value),
                            isLoading = false,
                            error = null,
                        ),
                    )
                }
                is ZephyrrResponse.Failure -> {
                    Timber.d("getSoonTM failure:$response")
                    // TODO: do I copy the existing response if the new call fails?
                    // Retryable errors?
                    _soonTMLandingState.emit(
                        _soonTMLandingState.value.copy(
                            error = response.throwable,
                            isLoading = false,
                        ),
                    )
                }
            }
        }
    }

    private fun getPopularTv() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching getPopularTv")
            when (val response = tmdbRepository.fetchPopularTv(1)) {
                is ZephyrrResponse.Success -> {
                    Timber.d("getPopularTv success:$response")
                    _popularTvLandingState.emit(
                        _popularTvLandingState.value.copy(
                            response = tvListMapper.mapToEntity(response.value),
                            isLoading = false,
                            error = null,
                        ),
                    )
                }
                is ZephyrrResponse.Failure -> {
                    Timber.d("getSoonTM failure:$response")
                    // TODO: do I copy the existing response if the new call fails?
                    // Retryable errors?
                    _popularTvLandingState.emit(
                        _popularTvLandingState.value.copy(
                            error = response.throwable,
                            isLoading = false,
                        ),
                    )
                }
            }
        }
    }

    private fun getTopRatedTv() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching getTopRatedTv")
            when (val response = tmdbRepository.fetchTopRatedTv(1)) {
                is ZephyrrResponse.Success -> {
                    Timber.d("getTopRatedTv success:$response")
                    _topRatedTvLandingState.emit(
                        _topRatedTvLandingState.value.copy(
                            response = tvListMapper.mapToEntity(response.value),
                            isLoading = false,
                            error = null,
                        ),
                    )
                }
                is ZephyrrResponse.Failure -> {
                    Timber.d("getSoonTM failure:$response")
                    // TODO: do I copy the existing response if the new call fails?
                    // Retryable errors?
                    _topRatedTvLandingState.emit(
                        _topRatedTvLandingState.value.copy(
                            error = response.throwable,
                            isLoading = false,
                        ),
                    )
                }
            }
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

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            dispatcher: CoroutineDispatcher,
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(dispatcher) as T
            }
        }
    }
}
