package greenberg.moviedbshell.dagger

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import greenberg.moviedbshell.ZephyrrApplication
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule {
    @Provides
    @Singleton
    fun provideAppContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideGridListToggleState(application: Application): () -> String {
        return {
            application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE)
                .getString(ZephyrrApplication.GRID_LIST_TOGGLE, ZephyrrApplication.GRID_LIST_DEFAULT_VALUE)!!
        }
    }
}
