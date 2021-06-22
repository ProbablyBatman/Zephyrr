package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.MovieListState
import greenberg.moviedbshell.view.SoonTMFragment
import greenberg.moviedbshell.viewmodel.base.BaseMovieListViewModel
import io.reactivex.schedulers.Schedulers

class SoonTMViewModel
@AssistedInject constructor(
    @Assisted override var state: MovieListState,
    private val TMDBService: TMDBService,
    private val mapper: MovieListMapper
) : BaseMovieListViewModel<MovieListState>(state) {

    @AssistedFactory
    interface Factory : BaseMovieListViewModel.Factory {
        fun create(state: MovieListState): SoonTMViewModel
    }

    init {
        fetchMovies()
    }

    override fun fetchMovies() {
        withState { state ->
            val totalPages = state.movieListResponse()?.totalPages
            if (totalPages != null && state.pageNumber >= totalPages) {
                state.copy(shouldShowMaxPages = true)
                return@withState
            }
            TMDBService
                .querySoonTM(state.pageNumber)
                .subscribeOn(Schedulers.io())
                .execute {
                    when (it) {
                        is Fail -> {
                            copy(
                                pageNumber = state.pageNumber,
                                movieListResponse = it,
                                movieList = state.movieList
                            )
                        }
                        is Success -> {
                            val pages = it()?.totalPages
                            copy(
                                pageNumber = state.pageNumber + 1,
                                movieListResponse = it,
                                movieList = state.movieList + mapper.mapToEntity(it()),
                                shouldShowMaxPages = pages != null && pages <= state.pageNumber
                            )
                        }
                        else -> copy(
                            movieListResponse = it
                        )
                    }
                }
                .disposeOnClear()
        }
    }

    companion object : MavericksViewModelFactory<SoonTMViewModel, MovieListState> {
        override fun create(viewModelContext: ViewModelContext, state: MovieListState): SoonTMViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<SoonTMFragment>().viewModelFactory
            return fragment.create(state)
        }
    }
}