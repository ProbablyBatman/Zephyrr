package greenberg.moviedbshell.MosbyImpl

import com.hannesdorfmann.mosby3.mvp.MvpView
import greenberg.moviedbshell.Models.PopularMoviesModels.PopularMovieResponse

interface PopularMoviesView : MvpView {
    fun showLoading(pullToRefresh: Boolean)
    fun showError(throwable: Throwable, pullToRefresh: Boolean)
    fun setMovies(response: PopularMovieResponse)
    fun addMovies(response: PopularMovieResponse)
    fun showMovies()
    //TODO: this is a temporary solution to pagination.  Instead of fronting a lot of effort which
    //is subject to change with the change of this app, just pop a snackbar for now.
    fun showPageLoad()
}