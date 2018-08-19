package greenberg.moviedbshell.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val appContext: Context) {
    @Provides
    @Singleton
    fun provideAppContext() = appContext
}