package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.PersonDetailMapper
import greenberg.moviedbshell.models.peopledetailmodels.CombinedCreditsResponse
import greenberg.moviedbshell.models.peopledetailmodels.PersonDetailResponse
import greenberg.moviedbshell.models.peopledetailmodels.PersonDetailResponseContainer
import greenberg.moviedbshell.models.ui.PersonDetailItem
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.PersonDetailState
import greenberg.moviedbshell.view.PersonDetailFragment
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class PersonDetailViewModel
@AssistedInject constructor(
    @Assisted var state: PersonDetailState,
    private val TMDBService: TMDBService,
    private val mapper: PersonDetailMapper
) : ZephyrrMvRxViewModel<PersonDetailState>(state) {

    @AssistedInject.Factory
    interface Factory {
        fun create(state: PersonDetailState): PersonDetailViewModel
    }

    init {
        logStateChanges()
    }

    fun fetchPersonDetail() {
        withState { state ->
            // Swallows requests if there's already one loading
            if (state.personDetailResponse is Loading) return@withState
            // TODO: reevaluate if these need subscribeOn
            Single.zip(
                            TMDBService.queryPersonDetail(state.personId).subscribeOn(Schedulers.io()),
                            TMDBService.queryPersonCombinedCredits(state.personId).subscribeOn(Schedulers.io()),
                            BiFunction<PersonDetailResponse, CombinedCreditsResponse, PersonDetailItem> { personDetail, personCredits ->
                                mapper.mapToEntity(PersonDetailResponseContainer(personDetail, personCredits))
                            }
                    )
                    .subscribeOn(Schedulers.io())
                    .execute {
                        copy(
                                personId = state.personId,
                                personDetailItem = it(),
                                personDetailResponse = it
                        )
                    }
                    .disposeOnClear()
        }
    }

    companion object : MvRxViewModelFactory<PersonDetailViewModel, PersonDetailState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: PersonDetailState): PersonDetailViewModel? {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<PersonDetailFragment>().personDetailViewModelFactory
            return fragment.create(state)
        }
    }
}