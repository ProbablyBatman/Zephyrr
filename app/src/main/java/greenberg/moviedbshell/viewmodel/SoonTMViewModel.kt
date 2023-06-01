package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.base.ZephyrrResponse
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.state.MovieListState
import greenberg.moviedbshell.view.SoonTMFragment
import greenberg.moviedbshell.viewmodel.base.BaseMovieListViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SoonTMViewModel
@AssistedInject constructor(
    @Assisted override val movieId: Int,
    @Assisted override val dispatcher: CoroutineDispatcher,
    private val tmdbRepository: TmdbRepository,
    private val mapper: MovieListMapper
) : BaseMovieListViewModel<MovieListState>(movieId, dispatcher) {

    private val _soonTMState = MutableStateFlow(MovieListState())
    val soonTMState: StateFlow<MovieListState> = _soonTMState.asStateFlow()

    @AssistedFactory
    interface Factory : BaseMovieListViewModel.Factory {
        fun create(movieId: Int, dispatcher: CoroutineDispatcher): SoonTMViewModel
    }

    init {
        fetchFirstPage()
    }

    override fun fetchMovies() {
        viewModelScope.launch(dispatcher) {
            val currentPageNumber = _soonTMState.value.pageNumber
            val previousState = _soonTMState.value
            _soonTMState.emit(previousState.copy(isLoading = true))
            when (val response = tmdbRepository.fetchPopularMovies(currentPageNumber)) {
                is ZephyrrResponse.Success -> {
                    val totalPages = response.value.totalPages
                    _soonTMState.emit(previousState.copy(
                        pageNumber = currentPageNumber + 1,
                        isLoading = false,
                        movieList = previousState.movieList + mapper.mapToEntity(response.value),
                        shouldShowMaxPages = totalPages != null && currentPageNumber >= totalPages,
                    ))
                }
                is ZephyrrResponse.Failure -> {
                    _soonTMState.emit(previousState.copy(
                        pageNumber = previousState.pageNumber,
                        isLoading = false,
                        error = response.throwable
                    ))
                }
            }
        }
    }

    private fun fetchFirstPage() {
        viewModelScope.launch(dispatcher) {
            val previousState = _soonTMState.value
            when (val response = tmdbRepository.fetchPopularMovies(1)) {
                is ZephyrrResponse.Success -> {
                    _soonTMState.emit(previousState.copy(
                        pageNumber = previousState.pageNumber + 1,
                        movieList = mapper.mapToEntity(response.value),
                        isLoading = false,
                        error = null,
                    ))
                }
                is ZephyrrResponse.Failure -> {
                    // Assumes the initial list is empty
                    _soonTMState.emit(previousState.copy(
                        pageNumber = 1,
                        isLoading = false,
                        error = response.throwable,
                    ))
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            movieId: Int,
            dispatcher: CoroutineDispatcher
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(movieId, dispatcher) as T
            }
        }
    }
}
