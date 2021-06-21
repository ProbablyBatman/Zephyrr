package greenberg.moviedbshell.base

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import greenberg.moviedbshell.BuildConfig

abstract class ZephyrrMvRxViewModel<S : MvRxState>(initialState: S) : BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG)