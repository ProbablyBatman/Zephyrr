package greenberg.moviedbshell.base

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel

abstract class ZephyrrMvRxViewModel<S : MavericksState>(initialState: S) : MavericksViewModel<S>(initialState)