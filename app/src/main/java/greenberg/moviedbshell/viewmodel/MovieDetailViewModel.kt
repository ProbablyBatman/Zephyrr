package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.MovieDetailMapper
import greenberg.moviedbshell.models.container.MovieDetailResponseContainer
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.MovieDetailState
import greenberg.moviedbshell.view.MovieDetailFragment
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class MovieDetailViewModel
@AssistedInject constructor(
    @Assisted var state: MovieDetailState,
    private val TMDBService: TMDBService,
    private val mapper: MovieDetailMapper
) : ZephyrrMvRxViewModel<MovieDetailState>(state) {

    @AssistedInject.Factory
    interface Factory {
        fun create(state: MovieDetailState): MovieDetailViewModel
    }

    init {
        logStateChanges()
        fetchMovieDetail()
    }

    fun fetchMovieDetail() {
        withState { state ->
            // Swallows requests if there's already one loading
            if (state.movieDetailResponse is Loading) return@withState
            Single.zip(
                TMDBService.queryMovieDetail(state.movieId),
                TMDBService.queryMovieCredits(state.movieId),
                TMDBService.queryMovieImages(state.movieId),
                { movieDetail, movieCredits, imageGallery ->
                    mapper.mapToEntity(MovieDetailResponseContainer(movieDetail, movieCredits, imageGallery))
                }
            )
                .subscribeOn(Schedulers.io())
                .execute {
                    // If call fails, should be retry-able
                    copy(
                        movieId = state.movieId,
                        movieDetailItem = it(),
                        movieDetailResponse = it
                    )
                }
                .disposeOnClear()
        }
    }

    companion object : MvRxViewModelFactory<MovieDetailViewModel, MovieDetailState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: MovieDetailState): MovieDetailViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<MovieDetailFragment>().movieDetailViewModelFactory
            return fragment.create(state)
        }
    }
}