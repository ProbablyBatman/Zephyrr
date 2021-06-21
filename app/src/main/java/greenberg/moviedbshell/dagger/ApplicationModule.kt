package greenberg.moviedbshell.dagger

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import greenberg.moviedbshell.ZephyrrApplication
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {
    @Provides
    @Singleton
    fun provideAppContext(): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideGridListToggleState(): () -> String {
        return {
            application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE)
                .getString(ZephyrrApplication.GRID_LIST_TOGGLE, ZephyrrApplication.GRID_LIST_DEFAULT_VALUE)!!
        }
    }
}