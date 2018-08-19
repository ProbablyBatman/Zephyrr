package greenberg.moviedbshell.dagger

import dagger.Component
import greenberg.moviedbshell.mosbyImpl.MovieDetailPresenter
import greenberg.moviedbshell.mosbyImpl.PopularMoviesPresenter
import greenberg.moviedbshell.mosbyImpl.SearchPresenter
import javax.inject.Singleton

@Component(modules = [(ApplicationModule::class), (ServicesModule::class)])
@Singleton
interface SingletonComponent {
    fun popularMoviesPresenter() : PopularMoviesPresenter
    fun searchPresenter(): SearchPresenter
    fun movieDetailPresenter(): MovieDetailPresenter
}