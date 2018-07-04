package greenberg.moviedbshell.mosbyImpl

import com.hannesdorfmann.mosby3.mvp.MvpView
import greenberg.moviedbshell.models.PopularMoviesModels.PopularMovieResultsItem

interface PopularMoviesView : MvpView {
    fun showLoading(pullToRefresh: Boolean)
    fun showError(throwable: Throwable, pullToRefresh: Boolean)
    fun setMovies(items: List<PopularMovieResultsItem?>)
    fun addMovies(items: List<PopularMovieResultsItem?>)
    fun showMovies()
    fun showDetail(fragment: MovieDetailFragment)
    //TODO: this is a temporary solution to pagination.  Instead of fronting a lot of effort which
    //is subject to change with the change of this app, just pop a snackbar for now.
    fun showPageLoad()
    fun hidePageLoad()
}