package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.mappers.SearchResultsMapper
import greenberg.moviedbshell.models.ui.PersonItem
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.state.MultiSearchState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MultiSearchViewModel
@AssistedInject constructor(
    @Assisted private val dispatcher: CoroutineDispatcher,
    private val tmdbRepository: TmdbRepository,
    private val mapper: SearchResultsMapper
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(dispatcher: CoroutineDispatcher): MultiSearchViewModel
    }

    private val _multiSearchState = MutableStateFlow(MultiSearchState())
    val multiSearchState = _multiSearchState.asStateFlow()

    private val currentQueries: MutableList<PersonItem> = mutableListOf()

    fun updateCurrentQuery(updatedQuery: String) {
        viewModelScope.launch(dispatcher) {
            _multiSearchState.emit(
                _multiSearchState.value.copy(
                    currentQuery = updatedQuery
                )
            )
        }
    }

    // TODO: nullable check here isn't necessary
    fun addQuery(query: PersonItem?) {
        viewModelScope.launch(dispatcher) {
            query?.let {
                if (!currentQueries.contains(query)) {
                    currentQueries.add(query)
                }
            }
            val previousState = _multiSearchState.value
            _multiSearchState.emit(
                previousState.copy(
                    currentQuery = "",
                    queries = currentQueries
                )
            )
        }
    }

    fun removeQuery(position: Int) {
        viewModelScope.launch(dispatcher) {
            currentQueries.removeAt(position)
            _multiSearchState.emit(
                _multiSearchState.value.copy(
                    queries = currentQueries
                )
            )
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
