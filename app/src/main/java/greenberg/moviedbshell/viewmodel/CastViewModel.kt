package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import greenberg.moviedbshell.models.ui.CastMemberItem
import greenberg.moviedbshell.state.CastState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CastViewModel(
    castMemberItems: List<CastMemberItem>,
) : ViewModel() {

    private val _castMemberState = MutableStateFlow(CastState(castMemberItems))
    val castState: StateFlow<CastState> = _castMemberState.asStateFlow()

    // TODO: I don't think anything else needs to be done here for now but keep my options open anyway

    companion object {
        fun provideFactory(castMemberItems: List<CastMemberItem>) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CastViewModel(castMemberItems) as T
            }
        }
    }
}
