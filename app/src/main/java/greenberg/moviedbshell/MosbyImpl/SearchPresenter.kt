package greenberg.moviedbshell.MosbyImpl

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.Models.SearchModels.SearchResultsItem
import greenberg.moviedbshell.RetrofitHelpers.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchPresenter : MvpBasePresenter<ZephyrrSearchView>() {

    private var TMDBService = RetrofitHelper().getTMDBService()
    private var isRecyclerLoading = false
    //Default to getting the first page
    private var searchResultsPageNumber = 1
    //It is enforced that this string is at least not null and blank
    private var lastQuery: String? = null
    private var searchResultsList = mutableListOf<SearchResultsItem?>()

    fun performSearch(query: String) {
        lastQuery = query
        searchResultsPageNumber = 1
        TMDBService.querySearchMulti(lastQuery!!, searchResultsPageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response -> ifViewAttached {
                        view: ZephyrrSearchView ->
                            response.results?.map { searchResultsList.add(it) }
                            view.setResults(searchResultsList)
                            view.showResults()
                    }
                }, {
                    throwable -> ifViewAttached {
                        view: ZephyrrSearchView ->
                            view.showError(throwable, false)
                    }
                })
    }

    fun initRecyclerPagination(recyclerView: RecyclerView?) {
        recyclerView?.apply {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = this@apply.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isRecyclerLoading
                            && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        ifViewAttached { view: ZephyrrSearchView ->
                            isRecyclerLoading = true
                            view.showPageLoad()
                            fetchNextPage(lastQuery!!)
                        }
                    }
                }
            })
        }
    }

    //Gets next page of search/multi movies call
    private fun fetchNextPage(query: String) {
        TMDBService.querySearchMulti(query, ++searchResultsPageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response -> ifViewAttached {
                        view: ZephyrrSearchView ->
                            response.results?.map { searchResultsList.add(it) }
                            view.addResults(searchResultsList)
                            view.hidePageLoad()
                            isRecyclerLoading = false
                    }
                }, {
                    throwable -> ifViewAttached {
                        view: ZephyrrSearchView ->
                            //todo: revisit erroring the whole page on this.  Just error bottom
                            view.showError(throwable, false)
                    }
                })
    }
}