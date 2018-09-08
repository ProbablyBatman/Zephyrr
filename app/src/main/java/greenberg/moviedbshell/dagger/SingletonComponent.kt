package greenberg.moviedbshell.dagger

import dagger.Component
import greenberg.moviedbshell.presenters.MovieDetailPresenter
import greenberg.moviedbshell.presenters.PersonDetailPresenter
import greenberg.moviedbshell.presenters.PopularMoviesPresenter
import greenberg.moviedbshell.presenters.SearchPresenter
import greenberg.moviedbshell.presenters.TvDetailPresenter
import javax.inject.Singleton

@Component(modules = [(ApplicationModule::class), (ServicesModule::class)])
@Singleton
interface SingletonComponent {
    fun popularMoviesPresenter(): PopularMoviesPresenter
    fun searchPresenter(): SearchPresenter
    fun movieDetailPresenter(): MovieDetailPresenter
    fun tvDetailPresenter(): TvDetailPresenter
    fun personDetailPresenter(): PersonDetailPresenter
}