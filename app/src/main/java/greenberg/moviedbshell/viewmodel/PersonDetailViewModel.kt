package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrResponse
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.state.PersonDetailState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class PersonDetailViewModel
@AssistedInject constructor(
    @Assisted private val personId: Int,
    @Assisted private val dispatcher: CoroutineDispatcher,
    private val tmdbRepository: TmdbRepository,
) : ViewModel() {

    private val _personDetailState = MutableStateFlow(
        PersonDetailState(
            personId = personId,
        ),
    )
    val personDetailState: StateFlow<PersonDetailState> = _personDetailState.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(personId: Int, dispatcher: CoroutineDispatcher): PersonDetailViewModel
    }

    init {
        fetchPersonDetail()
    }

    fun fetchPersonDetail() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching fetchPersonDetail")
            when (val response = tmdbRepository.fetchPersonDetail(this, personId)) {
                is ZephyrrResponse.Success -> {
                    _personDetailState.emit(
                        _personDetailState.value.copy(
                            personDetailItem = response.value,
                            isLoading = false,
                            error = null,
                        ),
                    )
                }
                is ZephyrrResponse.Failure -> {
                    _personDetailState.emit(
                        _personDetailState.value.copy(
                            personDetailItem = null,
                            isLoading = false,
                            error = response.throwable,
                        ),
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            personId: Int,
            dispatcher: CoroutineDispatcher,
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(personId, dispatcher) as T
            }
        }
    }
}
