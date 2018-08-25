package greenberg.moviedbshell.view

import com.hannesdorfmann.mosby3.mvp.MvpView
import greenberg.moviedbshell.models.ui.TvDetailItem

interface TvDetailView : MvpView {
    fun showLoading(tvShowId: Int)
    fun showError(throwable: Throwable)
    fun showTvDetails(tvDetailItem: TvDetailItem)
}