package greenberg.moviedbshell

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
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
    private var nightMode = false

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

        nightMode = retrieveSharedPreferences().getBoolean(NIGHT_MODE, false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun toggleNightMode() {
        nightMode = !nightMode
        retrieveSharedPreferences().edit().putBoolean(NIGHT_MODE, nightMode).apply()
        AppCompatDelegate.setDefaultNightMode(
                if (nightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun retrieveSharedPreferences() = this.getSharedPreferences(this.packageName, Context.MODE_PRIVATE)

    companion object {
        private const val NIGHT_MODE = "night_mode"
    }
}