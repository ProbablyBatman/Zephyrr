package greenberg.moviedbshell.MosbyImpl

import com.hannesdorfmann.mosby3.mvp.MvpView
import greenberg.moviedbshell.Models.PopularMoviesModels.PopularMovieResponse

interface PopularMoviesView : MvpView {
    fun showLoading(pullToRefresh: Boolean)
    fun showError(throwable: Throwable, pullToRefresh: Boolean)
    fun setMovies(response: PopularMovieResponse)
    fun addMovies(response: PopularMovieResponse)
    fun showMovies()
}