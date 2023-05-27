package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import greenberg.moviedbshell.state.CrewState

class CrewViewModel(
    state: CrewState
//) : ZephyrrMvRxViewModel<CrewState>(state)
) : ViewModel()