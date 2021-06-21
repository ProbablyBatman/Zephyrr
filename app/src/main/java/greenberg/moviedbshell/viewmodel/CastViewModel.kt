package greenberg.moviedbshell.viewmodel

import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.state.CastState

class CastViewModel(
    state: CastState
) : ZephyrrMvRxViewModel<CastState>(state) {
    init {
        logStateChanges()
    }
}