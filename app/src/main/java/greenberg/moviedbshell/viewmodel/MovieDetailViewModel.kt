package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrResponse
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.state.MovieDetailState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MovieDetailViewModel
@AssistedInject constructor(
    @Assisted private val movieId: Int,
    @Assisted private val dispatcher: CoroutineDispatcher,
    private val tmdbRepository: TmdbRepository,
) : ViewModel() {

    private val _movieDetailState = MutableStateFlow(
        MovieDetailState(
            movieId = movieId,
        ),
    )
    val movieDetailState: StateFlow<MovieDetailState> = _movieDetailState.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(movieId: Int, dispatcher: CoroutineDispatcher): MovieDetailViewModel
    }

    init {
        fetchMovieDetail()
    }

    fun fetchMovieDetail() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching fetchMovieDetail")
            when (val response = tmdbRepository.fetchMovieDetail(this, movieId)) {
                is ZephyrrResponse.Success -> {
                    _movieDetailState.emit(
                        _movieDetailState.value.copy(
                            movieDetailItem = response.value,
                            isLoading = false,
                            error = null,
                        ),
                    )
                }
                is ZephyrrResponse.Failure -> {
                    _movieDetailState.emit(
                        _movieDetailState.value.copy(
                            movieDetailItem = null,
                            isLoading = false,
                            error = response.throwable,
                        ),
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            movieId: Int,
            dispatcher: CoroutineDispatcher,
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(movieId, dispatcher) as T
            }
        }
    }
}
