package greenberg.moviedbshell

import MS_APPCENTER_KEY
import android.app.Application
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.AppCenter
import greenberg.moviedbshell.logging.DebuggingTree
import greenberg.moviedbshell.logging.NoLogTree
import timber.log.Timber

class ZephyrrApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebuggingTree())
        } else {
            Timber.plant(NoLogTree())
        }

        AppCenter.start(this,
                MS_APPCENTER_KEY,
                Analytics::class.java,
                Crashes::class.java)
    }
}