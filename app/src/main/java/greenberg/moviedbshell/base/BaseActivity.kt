package greenberg.moviedbshell.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        log("onBackPressed")
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
     * Designed to be overriden and used for logging.  This way, sub classes of this activity will show their own lifecycle in the logcat, as
     * opposed to this class.
     *
     * Example:
     * MainActivity onCreate will log MainActivity:xy: onCreate and will not show BaseActivity:xy: onCreate
     * in the logs.
     */
    abstract fun log(message: String)
}