package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.state.PersonDetailState
import greenberg.moviedbshell.view.PersonDetailFragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PersonDetailViewModel
@AssistedInject constructor(
    @Assisted var state: PersonDetailState,
    private val tmdbRepository: TmdbRepository,
) : ZephyrrMvRxViewModel<PersonDetailState>(state) {

    @AssistedFactory
    interface Factory {
        fun create(state: PersonDetailState): PersonDetailViewModel
    }

    init {
        fetchPersonDetail()
    }

    fun fetchPersonDetail(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        withState { state ->
            // Swallows requests if there's already one loading
            if (state.personDetailResponse is Loading) return@withState
            suspend { tmdbRepository.fetchPersonDetail(state.personId) }
                .execute(dispatcher) {
                    copy(
                        personId = state.personId,
                        personDetailItem = it(),
                        personDetailResponse = it
                    )
                }
        }
    }

    companion object : MavericksViewModelFactory<PersonDetailViewModel, PersonDetailState> {
        override fun create(viewModelContext: ViewModelContext, state: PersonDetailState): PersonDetailViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<PersonDetailFragment>().personDetailViewModelFactory
            return fragment.create(state)
        }
    }
}
