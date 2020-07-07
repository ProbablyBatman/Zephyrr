package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
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

    @AssistedInject.Factory
    interface Factory : BaseMovieListViewModel.Factory {
        fun create(state: MovieListState): SoonTMViewModel
    }

    init {
        logStateChanges()
        fetchMovies()
    }

    override fun fetchMovies() {
        withState { state ->
            TMDBService
                .querySoonTM(state.pageNumber)
                .subscribeOn(Schedulers.io())
                .execute {
                    val totalPages = it.invoke()?.totalPages
                    when {
                        totalPages != null && state.pageNumber > totalPages -> {
                            copy(
                                shouldShowMaxPages = true
                            )
                        }
                        it is Fail -> {
                            copy(
                                pageNumber = state.pageNumber,
                                movieListResponse = it,
                                movieList = state.movieList
                            )
                        }
                        it is Success -> {
                            copy(
                                pageNumber = state.pageNumber + 1,
                                movieListResponse = it,
                                movieList = state.movieList + mapper.mapToEntity(it())
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

    companion object : MvRxViewModelFactory<SoonTMViewModel, MovieListState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: MovieListState): SoonTMViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<SoonTMFragment>().viewModelFactory
            return fragment.create(state)
        }
    }
}