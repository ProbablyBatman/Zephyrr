package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.TvDetailMapper
import greenberg.moviedbshell.models.container.TvDetailResponseContainer
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.TvDetailState
import greenberg.moviedbshell.view.TvDetailFragment
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TvDetailViewModel
@AssistedInject constructor(
    @Assisted var state: TvDetailState,
    private val TMDBService: TMDBService,
    private val mapper: TvDetailMapper
) : ZephyrrMvRxViewModel<TvDetailState>(state) {

    @AssistedInject.Factory
    interface Factory {
        fun create(state: TvDetailState): TvDetailViewModel
    }

    init {
        logStateChanges()
        fetchTvDetail()
    }

    fun fetchTvDetail() {
        withState { state ->
            Single.zip(
                TMDBService.queryTvDetail(state.tvId),
                TMDBService.queryTvCredits(state.tvId),
                TMDBService.queryTvImages(state.tvId),
                TMDBService.queryTvAggregateCredits(state.tvId),
                { tvDetail, tvCredits, imageGalleryResponse, aggregateCredits ->
                    mapper.mapToEntity(TvDetailResponseContainer(tvDetail, tvCredits, aggregateCredits, imageGalleryResponse))
                }
            )
                .subscribeOn(Schedulers.io())
                .execute {
                    copy(
                        tvId = state.tvId,
                        tvDetailItem = it(),
                        tvDetailResponse = it
                    )
                }
                .disposeOnClear()
        }
    }

    companion object : MvRxViewModelFactory<TvDetailViewModel, TvDetailState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: TvDetailState): TvDetailViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<TvDetailFragment>().tvDetailViewModelFactory
            return fragment.create(state)
        }
    }
}