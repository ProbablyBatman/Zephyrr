package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrResponse
import greenberg.moviedbshell.mappers.SearchResultsMapper
import greenberg.moviedbshell.models.searchmodels.SearchResponse
import greenberg.moviedbshell.models.ui.PreviewItem
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.state.CombinedItemsState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

// TODO: Maybe fix naming of these files
class CombinedItemsListViewModel
@AssistedInject constructor(
    @Assisted private val ids: List<Int>,
    @Assisted private val dispatcher: CoroutineDispatcher,
    private val tmdbRepository: TmdbRepository,
    // TODO: investigate if search response for this is correct
    private val mapper: SearchResultsMapper,
) : ViewModel() {

    private val _combinedItemsState = MutableStateFlow(CombinedItemsState())
    val combinedItemsState: StateFlow<CombinedItemsState> = _combinedItemsState.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(ids: List<Int>, dispatcher: CoroutineDispatcher): CombinedItemsListViewModel
    }

    init {
        fetchItems()
    }

    fun fetchItems() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching fetchItems")
            _combinedItemsState.emit(
                _combinedItemsState.value.copy(
                    isLoading = true,
                ),
            )
            val previousState = _combinedItemsState.value
            var movieResponse: ZephyrrResponse<SearchResponse>? = null
            var tvResponse: ZephyrrResponse<SearchResponse>? = null
            if (!previousState.isTvMaxPages) {
                // TODO: no-op for now because apparently this doesn't exist
//                tvResponse = tmdbRepository.fetchTvDiscover(this, ids, previousState.tvPageNumber)
            }
            if (!previousState.isMovieMaxPages) {
                movieResponse = tmdbRepository.fetchMovieDiscover(this, ids, previousState.moviePageNumber)
            }
            // TODO: I'm just going to let one of these silently fail if a call happens to be non-functional.
            // I should probably do something else but I'm going to live with that

            var movieList = emptyList<PreviewItem>()
            var isMaxMoviePages = false
            var movieError: Throwable? = null

            var tvList = emptyList<PreviewItem>()
            var isMaxTvPages = false
            var tvError: Throwable? = null

            // TODO: this actually comes with a bunch of responses that aren't marked with a type. Going to map and apply a type manually here
            // This shouldn't have to be done but this is a fragile feature right now so I'll establish a better paradigm later.
            if (movieResponse != null) {
                when (movieResponse) {
                    is ZephyrrResponse.Success -> {
                        Timber.d("fetchMovieDiscover success:$movieResponse")
                        movieList = mapper.mapUnmarkedMovies(movieResponse.value)
                        // TODO: this is kind of stupid. Probably shouldn't be like this.
                        isMaxMoviePages = previousState.moviePageNumber >= (movieResponse.value.totalPages ?: -1)
                    }

                    is ZephyrrResponse.Failure -> {
                        Timber.d("fetchMovieDiscover failure:$movieResponse")
                        movieError = movieResponse.throwable
                    }
                }
            }
            if (tvResponse != null) {
                when (tvResponse) {
                    is ZephyrrResponse.Success -> {
                        Timber.d("fetchTvDiscover success:$tvResponse")
                        tvList = mapper.mapToEntity(tvResponse.value)
                        // TODO: this is kind of stupid. Probably shouldn't be like this.
                        isMaxTvPages = previousState.tvPageNumber >= (tvResponse.value.totalPages ?: -1)
                    }

                    is ZephyrrResponse.Failure -> {
                        Timber.d("fetchTvDiscover failure:$tvResponse")
                        tvError = tvResponse.throwable
                    }
                }
            }

            // TODO: fix this?
            // Attempt to sort this by id so that the results aren't always in movie then show format
            // sort by popularity for now? (this is default)
            val combinedResults = (movieList + tvList)

            when {
                movieError == null && tvError == null -> {
                    _combinedItemsState.emit(
                        previousState.copy(
                            moviePageNumber = previousState.moviePageNumber + 1,
                            tvPageNumber = previousState.tvPageNumber + 1,
                            isLoading = false,
                            combinedItemList = previousState.combinedItemList + combinedResults,
                            isMovieMaxPages = isMaxMoviePages,
                            isTvMaxPages = isMaxTvPages,
                        ),
                    )
                }
                // TODO: make movie error take the main assumption for now, but fix this in general.
                // maybe they should be separate?
                movieError != null && tvError != null -> {
                    _combinedItemsState.emit(
                        previousState.copy(
                            isLoading = false,
                            error = movieError,
                        ),
                    )
                }
                movieError != null -> {
                    _combinedItemsState.emit(
                        previousState.copy(
                            tvPageNumber = previousState.tvPageNumber + 1,
                            combinedItemList = previousState.combinedItemList + combinedResults,
                            isTvMaxPages = isMaxTvPages,
                            isLoading = false,
                            error = movieError,
                        ),
                    )
                }
                // tvError isn't null is the only remaining item
                else -> {
                    _combinedItemsState.emit(
                        previousState.copy(
                            moviePageNumber = previousState.moviePageNumber + 1,
                            combinedItemList = previousState.combinedItemList + combinedResults,
                            isMovieMaxPages = isMaxMoviePages,
                            isLoading = false,
                            error = tvError,
                        ),
                    )
                }
            }

//            when (val response = tmdbRepository.fetchIntersectingSearch(this, ids, previousState.pageNumber)) {
//                is ZephyrrResponse.Success -> {
//                    Timber.d("fetchItems success:$response")
//                    val movies = mapper.mapToEntity(response.value.movieResponse)
//                    val shows = mapper.mapToEntity(response.value.tvResponse)
//                    // TODO: fix this?
//                    // Attempt to sort this by id so that the results aren't always in movie then show format
//                    val results = movies + shows.sortedBy { it.id }
//                    val shouldShouldShowMaxPages = response.value.movieResponse.totalPages == response.
//                    _combinedItemsState.emit(_combinedItemsState.value.copy(
//                        combinedItemList = previousState.combinedItemList + results,
//                        pageNumber = previousState.pageNumber + 1,
//                        shouldShowMaxPages = response.value.movieResponse.totalPages,
//                        isLoading = false,
//                        error = null,
//                    ))
//                }
//                is ZephyrrResponse.Failure -> {
//                    Timber.d("fetchItems failure:$response")
//                    // TODO: do I copy the existing response if the new call fails?
//                    // Retryable errors?
//                    _combinedItemsState.emit(_combinedItemsState.value.copy(
//                        error = response.throwable,
//                        isLoading = false
//                    ))
//                }
//            }
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            ids: List<Int>,
            dispatcher: CoroutineDispatcher,
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(ids, dispatcher) as T
            }
        }
    }
}
