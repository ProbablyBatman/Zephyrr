package greenberg.moviedbshell.MosbyImpl

import com.hannesdorfmann.mosby3.mvp.MvpView
import greenberg.moviedbshell.Models.SearchModels.SearchResponse

//TODO: Find better naming scheme.  This is only named this because android has one.
// FIXME Why not ZephyrrSearchView?
interface MultiSearchView : MvpView {
    fun showLoading(pullToRefresh: Boolean)
    fun showError(throwable: Throwable, pullToRefresh: Boolean)
    fun setResults(response: SearchResponse)
    fun addResults(response: SearchResponse)
    fun showResults()
    //TODO: this is a temporary solution to pagination.  Instead of fronting a lot of effort which
    //is subject to change with the change of this app, just pop a snackbar for now.
    fun showPageLoad()
}
