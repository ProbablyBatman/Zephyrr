package greenberg.moviedbshell

import MS_APPCENTER_KEY
import android.app.Application
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.AppCenter

class ZephyrrApplication: Application() {

    override fun onCreate() {
        super.onCreate()



        AppCenter.start(this,
                MS_APPCENTER_KEY,
                Analytics::class.java,
                Crashes::class.java)
    }
}