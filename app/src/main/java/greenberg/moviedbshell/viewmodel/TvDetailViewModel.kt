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
import greenberg.moviedbshell.state.TvDetailState
import greenberg.moviedbshell.view.TvDetailFragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class TvDetailViewModel
@AssistedInject constructor(
    @Assisted private val tvId: Int,
    @Assisted private val dispatcher: CoroutineDispatcher,
    private val tmdbRepository: TmdbRepository,
) : ViewModel() {

    private val _tvDetailState = MutableStateFlow(
        TvDetailState(
            tvId = tvId,
        )
    )
    val tvDetailState: StateFlow<TvDetailState> = _tvDetailState.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(tvId: Int, dispatcher: CoroutineDispatcher): TvDetailViewModel
    }

    init {
        fetchTvDetail()
    }

    fun fetchTvDetail() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching fetchTvDetail")
            when (val response = tmdbRepository.fetchTvDetail(this, tvId)) {
                is ZephyrrResponse.Success -> {
                    _tvDetailState.emit(_tvDetailState.value.copy(
                        tvDetailItem = response.value,
                        isLoading = false,
                        error = null,
                    ))
                }
                is ZephyrrResponse.Failure -> {
                    _tvDetailState.emit(_tvDetailState.value.copy(
                        tvDetailItem = null,
                        isLoading = false,
                        error = response.throwable,
                    ))
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: TvDetailViewModel.Factory,
            tvId: Int,
            dispatcher: CoroutineDispatcher
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(tvId, dispatcher) as T
            }
        }
    }
}
