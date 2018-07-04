package greenberg.moviedbshell.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import timber.log.Timber

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Timber.d("onBackPressed")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }
}