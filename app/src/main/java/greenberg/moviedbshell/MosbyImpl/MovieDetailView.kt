package greenberg.moviedbshell.MosbyImpl

import com.hannesdorfmann.mosby3.mvp.MvpView
import greenberg.moviedbshell.Models.MovieDetailModels.MovieDetailResponse

interface MovieDetailView : MvpView {
    fun showLoading(movieId: Int)
    fun showError(throwable: Throwable)
    fun showMovieDetails(movieDetailResponse: MovieDetailResponse)
}