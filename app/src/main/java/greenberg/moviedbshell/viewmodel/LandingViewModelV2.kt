package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.mappers.TvListMapper
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.state.LandingPageUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LandingViewModelV2
@Inject constructor(
    private val tmdbRepository: TmdbRepository,
    private val movieListMapper: MovieListMapper,
    private val tvListMapper: TvListMapper
) : ViewModel() {

    private val _uiState: MutableStateFlow<LandingPageUiState> = MutableStateFlow(
        LandingPageUiState.Loading
    )
    val uiState: StateFlow<LandingPageUiState> = _uiState

    init {
        getLandingPageLists()
    }

    private fun getLandingPageLists() {
        try {
            viewModelScope.launch {
                try {
                    flow<LandingPageUiState> {
                        try {
                            // TODO: Can use async to do all the calls and make them start at the same time?
                            val popularMovieResponse =
                                async { tmdbRepository.fetchPopularMovies(1) }
                            val recentlyReleasedMovieResponse =
                                async { tmdbRepository.fetchRecentlyReleased(1) }
                            val soonTMMovieResponse = async { tmdbRepository.fetchSoonTM(1) }
                            val popularTVResponse = async { tmdbRepository.fetchPopularTv(1) }
                            val topRatedTVResponse = async { tmdbRepository.fetchTopRatedTv(1) }

                            emit(
//                                LandingPageUiState.Success(
//                                    false,
//                                    popularMovieList = movieListMapper.mapToEntity(
////                                        popularMovieResponse.await()
//                                        null
//                                    ),
//                                    recentlyReleasedMovieList = movieListMapper.mapToEntity(
////                                        recentlyReleasedMovieResponse.await()
//                                    ),
//                                    soonTMMovieList = movieListMapper.mapToEntity(
////                                        soonTMMovieResponse.await()
//                                    ),
//                                    popularTVList = tvListMapper.mapToEntity(
//                                        popularTVResponse.await()
//                                    ),
//                                    topRatedTVList = tvListMapper.mapToEntity(
//                                        topRatedTVResponse.await()
//                                    )
//                                )
                                LandingPageUiState.Loading
                            )
                        } catch (t: Throwable) {
                            Timber.e(t, "Emitting error from initial get 3")
                        }
                    }
                        .catch {
                            Timber.e(it, "Emitting error from initial get")
                            _uiState.value = LandingPageUiState.Failure(it)
                        }
                        .collect { _uiState.value = it }
                } catch (t: Throwable) {
                    Timber.e(t, "Emitting error from initial get 2")
                    _uiState.value = LandingPageUiState.Failure(t)
                }
            }
        } catch (t: Throwable) {
            Timber.e(t, "yo wtf")
        }
    }

    fun retryLandingPageLists() {
        Timber.d("${this.javaClass.name}:retryLandingPageLists")
        viewModelScope.launch { _uiState.emit(LandingPageUiState.Loading) }
        getLandingPageLists()
    }
}