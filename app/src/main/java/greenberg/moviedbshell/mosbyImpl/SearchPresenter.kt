package greenberg.moviedbshell.mosbyImpl

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.models.SearchModels.SearchResultsItem
import greenberg.moviedbshell.R
import greenberg.moviedbshell.retrofitHelpers.RetrofitHelper
import greenberg.moviedbshell.viewHolders.SearchResultsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat

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

    fun refreshView(adapter: SearchResultsAdapter?) {
        adapter?.searchResults?.clear()
        adapter?.notifyDataSetChanged()
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
                            //TODO: here and in the [PopularMoviePresenter], I changed this to set results for a bug fix.
                            //Instead, see about using add.  I lef it in in case there's another way it can work.
                            //If it can't, remove it from the interface view.
                            view.setResults(searchResultsList)
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

    fun fetchPosterArt(cardItemPosterView: ImageView, item: SearchResultsItem) {
        //Load poster art
        when (item.mediaType) {
            SearchResultsAdapter.MEDIA_TYPE_MOVIE, SearchResultsAdapter.MEDIA_TYPE_TV -> {
                item.posterPath
            }
            SearchResultsAdapter.MEDIA_TYPE_PERSON -> {
                item.profilePath
            }
            else -> {
                //Don't fetch poster if there is no poster art
                return
            }
        }?.let {
            Glide.with(cardItemPosterView)
                    //TODO: potentially hacky way to get context
                    .load(cardItemPosterView.context.getString(R.string.poster_url_substitution, it))
                    .apply { RequestOptions().centerCrop() }
                    .into(cardItemPosterView)
        }
    }

    fun processReleaseDate(releaseDate: String): String {
        return if (releaseDate.isNotBlank()) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = inputFormat.parse(releaseDate)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy")
            outputFormat.format(date)
        } else {
            return ""
        }
    }
}