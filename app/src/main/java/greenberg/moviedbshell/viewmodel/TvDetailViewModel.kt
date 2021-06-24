package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.TmdbRepository
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.state.TvDetailState
import greenberg.moviedbshell.view.TvDetailFragment

class TvDetailViewModel
@AssistedInject constructor(
    @Assisted var state: TvDetailState,
    private val tmdbRepository: TmdbRepository,
) : ZephyrrMvRxViewModel<TvDetailState>(state) {

    @AssistedFactory
    interface Factory {
        fun create(state: TvDetailState): TvDetailViewModel
    }

    init {
        fetchTvDetail()
    }

    fun fetchTvDetail() {
        withState { state ->
            suspend { tmdbRepository.fetchTvDetail(viewModelScope, state.tvId) }
                .execute {
                    copy(
                        tvId = state.tvId,
                        tvDetailItem = it(),
                        tvDetailResponse = it
                    )
                }
        }
    }

    companion object : MavericksViewModelFactory<TvDetailViewModel, TvDetailState> {
        override fun create(viewModelContext: ViewModelContext, state: TvDetailState): TvDetailViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<TvDetailFragment>().tvDetailViewModelFactory
            return fragment.create(state)
        }
    }
}