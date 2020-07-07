package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.SoonTMMapper
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.SoonTMState
import greenberg.moviedbshell.view.SoonTMFragment
import io.reactivex.schedulers.Schedulers

class SoonTMViewModel
@AssistedInject constructor(
    @Assisted var state: SoonTMState,
    private val TMDBService: TMDBService,
    private val mapper: SoonTMMapper
) : ZephyrrMvRxViewModel<SoonTMState>(state) {

    @AssistedInject.Factory
    interface Factory {
        fun create(state: SoonTMState): SoonTMViewModel
    }

    init {
        logStateChanges()
        fetchSoonTM()
    }

    fun fetchSoonTM() {
        withState { state ->
            TMDBService
                .querySoonTM(state.pageNumber)
                .subscribeOn(Schedulers.io())
                .execute {
                    // If call fails, pass the same state through, but it's a copy with the Async
                    // where Async is of Fail type.
                    if (it is Fail) {
                        copy(
                            pageNumber = state.pageNumber,
                            soonTMResponse = it,
                            soonTMMovies = state.soonTMMovies
                        )
                    } else {
                        copy(
                            pageNumber = state.pageNumber + 1,
                            soonTMResponse = it,
                            soonTMMovies = state.soonTMMovies + mapper.mapToEntity(it())
                        )
                    }
                }
                .disposeOnClear()
        }
    }

    companion object : MvRxViewModelFactory<SoonTMViewModel, SoonTMState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: SoonTMState): SoonTMViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<SoonTMFragment>().soonTMViewModelFactory
        return fragment.create(state)
    }
    }
}