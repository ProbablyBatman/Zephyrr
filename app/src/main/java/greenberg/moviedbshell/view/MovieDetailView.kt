package greenberg.moviedbshell.view

import com.hannesdorfmann.mosby3.mvp.MvpView
import greenberg.moviedbshell.models.ui.MovieDetailItem

interface MovieDetailView : MvpView {
    fun showLoading(movieId: Int)
    fun showError(throwable: Throwable)
    fun showMovieDetails(movieDetailItem: MovieDetailItem)
}