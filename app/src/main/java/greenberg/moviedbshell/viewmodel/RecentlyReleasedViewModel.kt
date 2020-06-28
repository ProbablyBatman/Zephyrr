package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.RecentlyReleasedMapper
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.RecentlyReleasedState
import greenberg.moviedbshell.view.RecentlyReleasedFragment
import io.reactivex.schedulers.Schedulers

class RecentlyReleasedViewModel
@AssistedInject constructor(
    @Assisted var state: RecentlyReleasedState,
    private val TMDBService: TMDBService,
    private val mapper: RecentlyReleasedMapper
) : ZephyrrMvRxViewModel<RecentlyReleasedState>(state) {

    @AssistedInject.Factory
    interface Factory {
        fun create(state: RecentlyReleasedState): RecentlyReleasedViewModel
    }

    init {
        logStateChanges()
        fetchRecentlyReleased()
    }

    fun fetchRecentlyReleased() {
        withState { state ->
            TMDBService
                .queryRecentlyReleased(state.pageNumber)
                .subscribeOn(Schedulers.io())
                .execute {
                    // If call fails, pass the same state through, but it's a copy with the Async
                    // where Async is of Fail type.
                    if (it is Fail) {
                        copy(
                            pageNumber = state.pageNumber,
                            recentlyReleasedResponse = it,
                            recentlyReleasedMovies = state.recentlyReleasedMovies
                        )
                    } else {
                        copy(
                            pageNumber = state.pageNumber + 1,
                            recentlyReleasedResponse = it,
                            recentlyReleasedMovies = state.recentlyReleasedMovies + mapper.mapToEntity(it())
                        )
                    }
                }
                .disposeOnClear()
        }
    }

    companion object : MvRxViewModelFactory<RecentlyReleasedViewModel, RecentlyReleasedState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: RecentlyReleasedState): RecentlyReleasedViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<RecentlyReleasedFragment>().recentlyReleasedViewModelFactory
            return fragment.create(state)
        }
    }
}