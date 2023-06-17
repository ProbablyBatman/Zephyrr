package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import greenberg.moviedbshell.models.ui.CrewMemberItem
import greenberg.moviedbshell.state.CrewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CrewViewModel(
    crewMemberItems: List<CrewMemberItem>,
) : ViewModel() {

    private val _crewMemberState = MutableStateFlow(CrewState(crewMemberItems))
    val crewMemberState: StateFlow<CrewState> = _crewMemberState.asStateFlow()

    companion object {
        fun provideFactory(crewMemberItems: List<CrewMemberItem>) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CrewViewModel(crewMemberItems) as T
            }
        }
    }
}
