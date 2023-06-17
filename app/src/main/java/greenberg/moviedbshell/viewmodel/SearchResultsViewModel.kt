package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrResponse
import greenberg.moviedbshell.mappers.SearchResultsMapper
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.state.SearchResultsState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchResultsViewModel
@AssistedInject constructor(
    @Assisted private val query: String,
    @Assisted private val dispatcher: CoroutineDispatcher,
    private val tmdbRepository: TmdbRepository,
    private val mapper: SearchResultsMapper,
) : ViewModel() {

    private val _searchResultState = MutableStateFlow(
        SearchResultsState(query),
    )
    val searchResultState = _searchResultState.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(query: String, dispatcher: CoroutineDispatcher): SearchResultsViewModel
    }

    init {
        fetchSearchResults()
    }

    // TODO: For multisearch, this should probably only search for people since that's all we're intersecting on
    fun fetchSearchResults() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching fetchSearchResults")
            _searchResultState.emit(
                _searchResultState.value.copy(
                    isLoading = true,
                ),
            )
            val previousState = _searchResultState.value
            when (val response = tmdbRepository.fetchSearchMulti(query, previousState.pageNumber)) {
                is ZephyrrResponse.Success -> {
                    Timber.d("fetchSearchResults success:$response")
                    _searchResultState.emit(
                        _searchResultState.value.copy(
                            searchResults = previousState.searchResults + mapper.mapToEntity(response.value),
                            pageNumber = previousState.pageNumber + 1,
                            isLoading = false,
                            error = null,
                        ),
                    )
                }
                is ZephyrrResponse.Failure -> {
                    Timber.d("fetchSearchResults failure:$response")
                    // TODO: do I copy the existing response if the new call fails?
                    // Retryable errors?
                    _searchResultState.emit(
                        _searchResultState.value.copy(
                            error = response.throwable,
                            isLoading = false,
                        ),
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            query: String,
            dispatcher: CoroutineDispatcher,
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(query, dispatcher) as T
            }
        }
    }
}
