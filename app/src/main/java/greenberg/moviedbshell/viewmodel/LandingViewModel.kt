package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.MovieListMapper
import greenberg.moviedbshell.mappers.TvListMapper
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.LandingState
import greenberg.moviedbshell.view.LandingFragment
import io.reactivex.schedulers.Schedulers

class LandingViewModel
@AssistedInject constructor(
    @Assisted var initialState: LandingState,
    private val TMDBService: TMDBService,
    private val movieListMapper: MovieListMapper,
    private val tvListMapper: TvListMapper
) : ZephyrrMvRxViewModel<LandingState>(initialState) {

    @AssistedFactory
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
        TMDBService.queryRecentlyReleased(1)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    recentlyReleasedResponse = it,
                    recentlyReleasedItems = movieListMapper.mapToEntity(it())
                )
            }
            .disposeOnClear()
    }

    fun getPopularMovies() {
        TMDBService.queryPopularMovies(1)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    popularMovieResponse = it,
                    popularMovieItems = movieListMapper.mapToEntity(it())
                )
            }
            .disposeOnClear()
    }

    fun getSoonTM() {
        TMDBService.querySoonTM(1)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    soonTMResponse = it,
                    soonTMItems = movieListMapper.mapToEntity(it())
                )
            }
            .disposeOnClear()
    }

    fun getPopularTv() {
        TMDBService.queryPopularTv(1)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    popularTvResponse = it,
                    popularTvItems = tvListMapper.mapToEntity(it())
                )
            }
            .disposeOnClear()
    }

    fun getTopRatedTv() {
        TMDBService.queryTopRatedTv(1)
            .subscribeOn(Schedulers.io())
            .execute {
                copy(
                    topRatedTvResponse = it,
                    topRatedTvItems = tvListMapper.mapToEntity(it())
                )
            }
            .disposeOnClear()
    }

    companion object : MvRxViewModelFactory<LandingViewModel, LandingState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: LandingState): LandingViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<LandingFragment>().landingViewModelFactory
            return fragment.create(state)
        }
    }
}