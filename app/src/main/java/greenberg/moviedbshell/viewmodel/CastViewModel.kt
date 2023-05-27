package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import greenberg.moviedbshell.state.CastState

class CastViewModel(
    state: CastState
//) : ZephyrrMvRxViewModel<CastState>(state)
) : ViewModel()
