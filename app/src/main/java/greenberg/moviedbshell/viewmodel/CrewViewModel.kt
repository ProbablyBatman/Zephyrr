package greenberg.moviedbshell.viewmodel

import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.state.CrewState

class CrewViewModel(
    state: CrewState
) : ZephyrrMvRxViewModel<CrewState>(state) {
    init {
        logStateChanges()
    }
}