package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrResponse
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.state.MovieListState
import greenberg.moviedbshell.viewmodel.base.BaseMovieListViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecentlyReleasedViewModel
@AssistedInject constructor(
    @Assisted override val movieId: Int,
    @Assisted override val dispatcher: CoroutineDispatcher,
    private val tmdbRepository: TmdbRepository,
    private val mapper: MovieListMapper
) : BaseMovieListViewModel<MovieListState>(movieId, dispatcher) {

    private val _recentlyReleasedState = MutableStateFlow(MovieListState())
    val recentlyReleasedState: StateFlow<MovieListState> = _recentlyReleasedState.asStateFlow()

    @AssistedFactory
    interface Factory : BaseMovieListViewModel.Factory {
        fun create(movieId: Int, dispatcher: CoroutineDispatcher): RecentlyReleasedViewModel
    }

    init {
        fetchFirstPage()
    }

    override fun fetchMovies() {
        viewModelScope.launch(dispatcher) {
            val currentPageNumber = _recentlyReleasedState.value.pageNumber
            val previousState = _recentlyReleasedState.value
            _recentlyReleasedState.emit(previousState.copy(isLoading = true))
            when (val response = tmdbRepository.fetchRecentlyReleased(currentPageNumber)) {
                is ZephyrrResponse.Success -> {
                    val totalPages = response.value.totalPages
                    _recentlyReleasedState.emit(previousState.copy(
                        pageNumber = currentPageNumber + 1,
                        isLoading = false,
                        movieList = previousState.movieList + mapper.mapToEntity(response.value),
                        shouldShowMaxPages = totalPages != null && currentPageNumber >= totalPages,
                    ))
                }
                is ZephyrrResponse.Failure -> {
                    _recentlyReleasedState.emit(previousState.copy(
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
            val previousState = _recentlyReleasedState.value
            when (val response = tmdbRepository.fetchRecentlyReleased(1)) {
                is ZephyrrResponse.Success -> {
                    _recentlyReleasedState.emit(previousState.copy(
                        pageNumber = previousState.pageNumber + 1,
                        movieList = mapper.mapToEntity(response.value),
                        isLoading = false,
                        error = null,
                    ))
                }
                is ZephyrrResponse.Failure -> {
                    // Assumes the initial list is empty
                    _recentlyReleasedState.emit(previousState.copy(
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
