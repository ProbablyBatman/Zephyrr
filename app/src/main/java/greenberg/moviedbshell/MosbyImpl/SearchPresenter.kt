package greenberg.moviedbshell.MosbyImpl

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.RetrofitHelpers.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchPresenter : MvpBasePresenter<MultiSearchView>() {

    private var TMDBService = RetrofitHelper().getTMDBService()
    private var isRecyclerLoading = false
    //Default to getting the first page
    private var searchResultsPageNumber = 1
    //It is enforced that this string is at least not null and blank
    private var lastQuery: String? = null

    fun performSearch(query: String) {
        lastQuery = query
        searchResultsPageNumber = 1
        TMDBService.querySearchMulti(lastQuery!!, searchResultsPageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response -> ifViewAttached {
                        view: MultiSearchView ->
                            view.setResults(response)
                            view.showResults()
                    }
                }, {
                    throwable -> ifViewAttached {
                        view: MultiSearchView ->
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
                        ifViewAttached { view: MultiSearchView ->
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
                        view: MultiSearchView ->
                            isRecyclerLoading = true
                            view.addResults(response)
                            isRecyclerLoading = false
                    }
                }, {
                    throwable -> ifViewAttached {
                        view: MultiSearchView ->
                            //todo: revisit erroring the whole page on this.  Just error bottom
                            view.showError(throwable, false)
                    }
                })
    }
}