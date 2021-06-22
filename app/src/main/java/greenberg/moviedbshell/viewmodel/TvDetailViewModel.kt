package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

    @AssistedFactory
    interface Factory {
        fun create(state: TvDetailState): TvDetailViewModel
    }

    init {
        fetchTvDetail()
    }

    fun fetchTvDetail() {
        viewModelScope
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

    companion object : MavericksViewModelFactory<TvDetailViewModel, TvDetailState> {
        override fun create(viewModelContext: ViewModelContext, state: TvDetailState): TvDetailViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<TvDetailFragment>().tvDetailViewModelFactory
            return fragment.create(state)
        }
    }
}