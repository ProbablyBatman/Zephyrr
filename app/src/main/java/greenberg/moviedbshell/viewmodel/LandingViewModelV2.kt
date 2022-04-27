package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.models.movielistmodels.MovieListResponse
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.state.MovieListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModelV2
@Inject constructor(
    private val tmdbRepository: TmdbRepository,
    private val mapper: MovieListMapper
) : ViewModel() {

    private val _uiState: MutableStateFlow<MovieListUiState> = MutableStateFlow(
        MovieListUiState.Success(
            1,
            true,
            emptyList(),
            false
        )
    )
    val uiState: StateFlow<MovieListUiState> = _uiState

    init {
        getMovieList(tmdbRepository::fetchPopularMovies)
    }

    private fun getMovieList(getMovieType: suspend (Int) -> MovieListResponse) {
        viewModelScope.launch {
            flow<MovieListUiState> {
                val response = getMovieType(1)
                val entity = mapper.mapToEntity(response)
                emit(
                    MovieListUiState.Success(
                        1,
                        false,
                        entity,
                        // Check to see if the total pages from response is greater than 1, just in case
                        response.totalPages != null && 1 >= response.totalPages
                    )
                )
            }
                .catch { emit(MovieListUiState.Failure(it)) }
                .collect { _uiState.value = it }
        }
    }
}