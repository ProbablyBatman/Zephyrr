package greenberg.moviedbshell.mosbyImpl

import com.hannesdorfmann.mosby3.mvp.MvpView
import greenberg.moviedbshell.models.MovieDetailModels.MovieDetailResponse

interface MovieDetailView : MvpView {
    fun showLoading(movieId: Int)
    fun showError(throwable: Throwable)
    fun showMovieDetails(movieDetailResponse: MovieDetailResponse)
}