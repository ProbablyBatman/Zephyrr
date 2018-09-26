package greenberg.moviedbshell

import android.app.Application
import com.crashlytics.android.Crashlytics
import greenberg.moviedbshell.dagger.ApplicationModule
import greenberg.moviedbshell.dagger.DaggerSingletonComponent
import greenberg.moviedbshell.dagger.SingletonComponent
import greenberg.moviedbshell.logging.DebuggingTree
import greenberg.moviedbshell.logging.NoLogTree
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class ZephyrrApplication : Application() {

    lateinit var component: SingletonComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebuggingTree())
        } else {
            Timber.plant(NoLogTree())
        }

        Fabric.with(this, Crashlytics())

        component = DaggerSingletonComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }
}