package greenberg.moviedbshell.base

import android.os.Bundle
import android.view.View
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpDialogFragment
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.hannesdorfmann.mosby3.mvp.MvpView

abstract class BaseDialogFragment<V : MvpView, P : MvpBasePresenter<V>> :
        MvpDialogFragment<V, P>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
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
}