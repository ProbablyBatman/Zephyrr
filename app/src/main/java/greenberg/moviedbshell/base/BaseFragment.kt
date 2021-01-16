package greenberg.moviedbshell.base

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx
import greenberg.moviedbshell.R

abstract class BaseFragment : BaseMvRxFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_bottom)
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }

    override fun onPause() {
        log("onPause")
        super.onPause()
    }

    override fun onStop() {
        log("onStop")
        super.onStop()
    }

    override fun onDestroy() {
        log("onDestroy")
        super.onDestroy()
    }

    /*
     * Designed to be overriden and used for logging.  This way, sub classes of this fragment will show their own lifecycle in the logcat, as
     * opposed to this class.
     *
     * Example:
     * MovieDetailFragment onCreate will log MovieDetailFragment:xy: onCreate and will not show BaseFragment:xy: onCreate
     * in the logs.
     */
    abstract fun log(message: String)
    abstract fun log(throwable: Throwable)

    protected fun navigate(@IdRes id: Int, args: Parcelable? = null) {
        findNavController().navigate(id, Bundle().apply { putParcelable(MvRx.KEY_ARG, args) })
    }
}