package greenberg.moviedbshell.view

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.MvpView

interface PopularMoviesView : MvpView {
    fun showLoading(pullToRefresh: Boolean)
    fun showError(throwable: Throwable, pullToRefresh: Boolean)
    fun showMovies()
    fun showDetail(bundle: Bundle)
    fun showMaxPages()
    fun hideMaxPages()
    // TODO: this is a temporary solution to pagination.  Instead of fronting a lot of effort which
    // is subject to change with the change of this app, just pop a snackbar for now.
    fun showPageLoad()
    fun hidePageLoad()
}