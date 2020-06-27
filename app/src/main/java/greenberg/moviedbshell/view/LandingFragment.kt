package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.state.shouldShowError
import greenberg.moviedbshell.state.shouldShowLoading
import greenberg.moviedbshell.viewmodel.LandingViewModel
import timber.log.Timber

class LandingFragment  : BaseFragment() {

    //TODO: INVESTIGATE REPLACING THIS WITH LATEINIT VAR INJECT
    val landingViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.landingViewModelFactory
    }

    private val viewModel: LandingViewModel by fragmentViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.landing_page_layout, container, false)
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            when {
                state.shouldShowError() -> {

                }
                state.shouldShowLoading() -> {

                }
                else -> {

                }
            }
        }
    }

    override fun log(message: String) {
        Timber.d(message)
    }
}