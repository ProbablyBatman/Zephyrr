package greenberg.moviedbshell

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import greenberg.moviedbshell.dagger.ApplicationModule
import greenberg.moviedbshell.dagger.DaggerSingletonComponent
import greenberg.moviedbshell.dagger.SingletonComponent
import greenberg.moviedbshell.logging.DebuggingTree
import greenberg.moviedbshell.logging.NoLogTree
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

        component = DaggerSingletonComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()

        nightMode = retrieveSharedPreferences().getBoolean(NIGHT_MODE, false)
        Timber.d("$nightMode Status")
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun toggleNightMode() {
        nightMode = !nightMode
        Timber.d("$nightMode Status update")
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