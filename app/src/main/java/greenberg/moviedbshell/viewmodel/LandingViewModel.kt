package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.LandingMapper
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.mappers.TvListMapper

import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.LandingState
import greenberg.moviedbshell.view.LandingFragment

class LandingViewModel
@AssistedInject constructor(
    @Assisted var initialState: LandingState,
    private val TMDBService: TMDBService,
    private val movieListMapper: MovieListMapper,
    private val tvListMapper: TvListMapper
) : ZephyrrMvRxViewModel<LandingState>(initialState) {

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: LandingState): LandingViewModel
    }

    init {
        logStateChanges()
        fetchLandingResults()
    }

    fun fetchLandingResults() {
        withState { state ->
            getRecentlyReleased()
            getPopularMovies()
            getSoonTM()
            getPopularTv()
            getTopRatedTv()
        }
    }

    fun getRecentlyReleased() {
        TMDBService.queryRecentlyReleased(1).execute {
            copy(
                recentlyReleasedResponse = it,
                recentlyReleasedItems = movieListMapper.mapToEntity(it())
            )
        }.disposeOnClear()
    }

    fun getPopularMovies() {
        TMDBService.queryPopularMovies(1).execute {
            copy(
                popularMovieResponse = it,
                popularMovieItems = movieListMapper.mapToEntity(it())
            )
        }.disposeOnClear()
    }

    fun getSoonTM() {
        TMDBService.querySoonTM(1).execute {
            copy(
                soonTMResponse = it,
                soonTMItems = movieListMapper.mapToEntity(it())
            )
        }.disposeOnClear()
    }

    fun getPopularTv() {
        TMDBService.queryPopularTv(1).execute {
            copy(
                popularTvResponse = it,
                popularTvItems = tvListMapper.mapToEntity(it())
            )
        }.disposeOnClear()
    }

    fun getTopRatedTv() {
        TMDBService.queryTopRatedTv(1).execute {
            copy(
                topRatedTvResponse = it,
                topRatedTvItems = tvListMapper.mapToEntity(it())
            )
        }.disposeOnClear()
    }

    companion object : MvRxViewModelFactory<LandingViewModel, LandingState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: LandingState): LandingViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<LandingFragment>().landingViewModelFactory
            return fragment.create(state)
        }
    }
}