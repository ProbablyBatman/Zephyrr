package greenberg.moviedbshell.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import com.airbnb.mvrx.MavericksView
import java.util.UUID

abstract class BaseDialogFragment : DialogFragment(), MavericksView {
    override val mvrxViewId by lazy { backdropViewUUID }
    private lateinit var backdropViewUUID: String

    override val subscriptionLifecycleOwner: LifecycleOwner
        get() = this.viewLifecycleOwnerLiveData.value ?: this

    override fun onCreate(savedInstanceState: Bundle?) {
        backdropViewUUID = savedInstanceState?.getString(DIALOG_KEY)
            ?: "${this.javaClass.name}.${UUID.randomUUID()}"
        super.onCreate(savedInstanceState)
        log("onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(DIALOG_KEY, backdropViewUUID)
        log("onSaveInstanceState")
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

    abstract fun log(message: String)
    abstract fun log(exception: Throwable)

    companion object {
        private const val DIALOG_KEY = "DIALOG_KEY"
    }
}
