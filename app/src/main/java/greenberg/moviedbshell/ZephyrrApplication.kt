package greenberg.moviedbshell

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.EntryPoints
import dagger.hilt.android.HiltAndroidApp
import greenberg.moviedbshell.dagger.ApplicationModule
import greenberg.moviedbshell.dagger.SingletonComponent
import greenberg.moviedbshell.logging.DebuggingTree
import greenberg.moviedbshell.logging.NoLogTree
import timber.log.Timber

@HiltAndroidApp
class ZephyrrApplication : Application() {
    lateinit var component: SingletonComponent
    private var nightMode = true
    private var gridListMode = "grid"

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebuggingTree())
        } else {
            Timber.plant(NoLogTree())
        }

        nightMode = retrieveSharedPreferences().getBoolean(NIGHT_MODE, true)
        gridListMode = retrieveSharedPreferences().getString(GRID_LIST_TOGGLE, GRID_LIST_DEFAULT_VALUE).orEmpty()
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

    fun toggleGridListView() {
        gridListMode = if (gridListMode == GRID_LIST_GRID_VALUE) GRID_LIST_LIST_VALUE else GRID_LIST_GRID_VALUE
        Timber.d("grid/list toggle is $gridListMode")
        retrieveSharedPreferences().edit().putString(GRID_LIST_TOGGLE, gridListMode).apply()
    }

    private fun retrieveSharedPreferences() = this.getSharedPreferences(this.packageName, Context.MODE_PRIVATE)

    fun component(): dagger.hilt.components.SingletonComponent {
        return EntryPoints.get(this, dagger.hilt.components.SingletonComponent::class.java)
    }

    companion object {
        private const val NIGHT_MODE = "night_mode"
        const val GRID_LIST_TOGGLE = "grid_list_toggle"
        const val GRID_LIST_DEFAULT_VALUE = "grid"
        const val GRID_LIST_GRID_VALUE = "grid"
        const val GRID_LIST_LIST_VALUE = "list"
    }
}
