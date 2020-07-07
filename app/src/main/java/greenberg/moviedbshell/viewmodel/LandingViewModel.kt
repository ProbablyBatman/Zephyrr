package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.LandingMapper
import greenberg.moviedbshell.models.container.LandingContainer
import greenberg.moviedbshell.models.movielistmodels.MovieListResponse
import greenberg.moviedbshell.models.popularmoviesmodels.PopularMovieResponse
import greenberg.moviedbshell.models.ui.LandingItem
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.LandingState
import greenberg.moviedbshell.view.LandingFragment
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers

class LandingViewModel
@AssistedInject constructor(
    @Assisted var initialState: LandingState,
    private val TMDBService: TMDBService,
    private val mapper: LandingMapper
) : ZephyrrMvRxViewModel<LandingState>(initialState) {

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: LandingState): LandingViewModel
    }

    init {
        logStateChanges()
        /**
         * This makes the naive assumption that all 3 will be available. I'm just assuming that
         * if one isn't available, the API is down because I don't expect a single endpoint to really
         * fail on something this "small".
         */
        fetchLandingResults()
    }

    fun fetchLandingResults() {
        withState { state ->
            Single.zip(
                    TMDBService.queryRecentlyReleased(1),
                    TMDBService.queryPopularMovies(1),
                    TMDBService.querySoonTM(1),
                    Function3<MovieListResponse, MovieListResponse, MovieListResponse, LandingItem> { recentlyReleasedResponse, popularMovieResponse, soonTMResponse ->
                        mapper.mapToEntity(
                            LandingContainer(
                                recentlyReleasedResponse,
                                popularMovieResponse,
                                soonTMResponse
                            )
                        )
                    }
            )
                    .subscribeOn(Schedulers.io())
                    .execute {
                        // TODO: If this call fails, should be retry-able. Might wanna match with other fragments
                        copy(
                            landingItem = it
                        )
                    }
                    .disposeOnClear()
        }
    }

    companion object : MvRxViewModelFactory<LandingViewModel, LandingState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: LandingState): LandingViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<LandingFragment>().landingViewModelFactory
            return fragment.create(state)
        }
    }
}