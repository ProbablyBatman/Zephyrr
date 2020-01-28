package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.SearchResultsMapper
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.SearchResultsState
import greenberg.moviedbshell.view.SearchResultsFragment
import io.reactivex.schedulers.Schedulers

class SearchResultsViewModel
@AssistedInject constructor(
    @Assisted state: SearchResultsState,
    private val TMDBService: TMDBService,
    private val mapper: SearchResultsMapper
) : ZephyrrMvRxViewModel<SearchResultsState>(state) {

    @AssistedInject.Factory
    interface Factory {
        fun create(state: SearchResultsState): SearchResultsViewModel
    }

    init {
        logStateChanges()
        fetchSearchResults()
    }

    fun fetchSearchResults() {
        withState { state ->
            if (state.searchResultsResponse is Loading) return@withState
            TMDBService
                    .querySearchMulti(state.query, state.pageNumber)
                    .subscribeOn(Schedulers.io())
                    .execute {
                        if (it is Fail) {
                            // Set results and pageNumber to the same thing so call can be retried
                            copy(
                                    query = state.query,
                                    pageNumber = state.pageNumber,
                                    totalPages = state.totalPages,
                                    searchResults = state.searchResults,
                                    searchResultsResponse = it
                            )
                        } else {
                            copy(
                                    query = state.query,
                                    pageNumber = state.pageNumber + 1,
                                    totalPages = it()?.totalPages ?: -1,
                                    searchResults = state.searchResults + mapper.mapToEntity(it()),
                                    searchResultsResponse = it
                            )
                        }
                    }
                    .disposeOnClear()
        }
    }

    fun fetchFirstPage() {
        withState { state ->
            // if (state.searchResultsResponse is Loading) return@withState
            TMDBService
                    .querySearchMulti(state.query, 1)
                    .subscribeOn(Schedulers.io())
                    .execute {
                        if (it is Fail) {
                            copy(
                                    query = state.query,
                                    pageNumber = 1,
                                    searchResults = emptyList(),
                                    totalPages = -1,
                                    searchResultsResponse = state.searchResultsResponse
                            )
                        } else {
                            copy(
                                    query = state.query,
                                    pageNumber = state.pageNumber + 1,
                                    searchResults = mapper.mapToEntity(it()),
                                    totalPages = it()?.totalPages ?: -1,
                                    searchResultsResponse = it
                            )
                        }
                    }
        }
    }

    companion object : MvRxViewModelFactory<SearchResultsViewModel, SearchResultsState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: SearchResultsState): SearchResultsViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<SearchResultsFragment>().searchResultsViewModelFactory
            return fragment.create(state)
        }
    }
}