package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.TmdbRepository
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.SearchResultsMapper
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.SearchResultsState
import greenberg.moviedbshell.view.SearchResultsFragment
import io.reactivex.schedulers.Schedulers

class SearchResultsViewModel
@AssistedInject constructor(
    @Assisted state: SearchResultsState,
    private val tmdbRepository: TmdbRepository,
    private val mapper: SearchResultsMapper
) : ZephyrrMvRxViewModel<SearchResultsState>(state) {

    @AssistedFactory
    interface Factory {
        fun create(state: SearchResultsState): SearchResultsViewModel
    }

    init {
        fetchSearchResults()
    }

    fun fetchSearchResults() {
        withState { state ->
            if (state.searchResultsResponse is Loading) return@withState
            suspend { tmdbRepository.fetchSearchMulti(state.query, state.pageNumber) }
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
        }
    }

    fun fetchFirstPage() {
//        withState { state ->
//            // if (state.searchResultsResponse is Loading) return@withState
//            TMDBService
//                    .querySearchMulti(state.query, 1)
//                    .subscribeOn(Schedulers.io())
//                    .execute {
//                        if (it is Fail) {
//                            copy(
//                                    query = state.query,
//                                    pageNumber = 1,
//                                    searchResults = emptyList(),
//                                    totalPages = -1,
//                                    searchResultsResponse = state.searchResultsResponse
//                            )
//                        } else {
//                            copy(
//                                    query = state.query,
//                                    pageNumber = state.pageNumber + 1,
//                                    searchResults = mapper.mapToEntity(it()),
//                                    totalPages = it()?.totalPages ?: -1,
//                                    searchResultsResponse = it
//                            )
//                        }
//                    }
//        }
    }

    companion object : MavericksViewModelFactory<SearchResultsViewModel, SearchResultsState> {
        override fun create(viewModelContext: ViewModelContext, state: SearchResultsState): SearchResultsViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<SearchResultsFragment>().searchResultsViewModelFactory
            return fragment.create(state)
        }
    }
}