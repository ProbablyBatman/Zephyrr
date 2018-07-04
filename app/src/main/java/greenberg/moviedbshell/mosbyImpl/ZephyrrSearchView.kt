package greenberg.moviedbshell.mosbyImpl

import com.hannesdorfmann.mosby3.mvp.MvpView
import greenberg.moviedbshell.models.SearchModels.SearchResultsItem

//TODO: Find better naming scheme.  This is only named this because android has one.
// FIXME Why not ZephyrrSearchView?
interface ZephyrrSearchView : MvpView {
    fun showLoading(pullToRefresh: Boolean)
    fun showError(throwable: Throwable, pullToRefresh: Boolean)
    fun setResults(items: List<SearchResultsItem?>)
    fun addResults(items: List<SearchResultsItem?>)
    fun showResults()
    //TODO: this is a temporary solution to pagination.  Instead of fronting a lot of effort which
    //is subject to change with the change of this app, just pop a snackbar for now.
    fun showPageLoad()
    fun hidePageLoad()
}
