package greenberg.moviedbshell.mosbyImpl

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.SearchModels.SearchResultsItem
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.viewHolders.SearchResultsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SearchPresenter
@Inject constructor(private val TMDBService: TMDBService) : MvpBasePresenter<ZephyrrSearchView>() {

    private var isRecyclerLoading = false
    //Default to getting the first page
    private var searchResultsPageNumber = 1
    private var totalAvailablePages = -1
    //It is enforced that this string is at least not null and blank
    private var lastQuery: String? = null
    private var searchResultsList = mutableListOf<SearchResultsItem?>()
    private var searchResultAdapter: SearchResultsAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var compositeDisposable = CompositeDisposable()
    private var loadedMaxPages = false

    override fun attachView(view: ZephyrrSearchView) {
        super.attachView(view)
        Timber.d("attachView")
        if (searchResultsList.isEmpty()) {
            view.showLoading()
        }
    }

    fun initRecyclerPagination(recyclerView: RecyclerView?, adapter: SearchResultsAdapter?) {
        this.recyclerView = recyclerView
        searchResultAdapter = adapter
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
                            if (!loadedMaxPages) {
                                view.showPageLoad()
                                fetchNextPage(lastQuery!!)
                            }
                        }
                    }
                }
            })
        }
    }

    //Gets next page of search/multi movies call
    private fun fetchNextPage(query: String) {
        if (searchResultsPageNumber < totalAvailablePages && totalAvailablePages != -1) {
            val disposable =
                    TMDBService.querySearchMulti(query, ++searchResultsPageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ response ->
                                ifViewAttached { view: ZephyrrSearchView ->
                                    response.results?.map { searchResultAdapter?.searchResults?.add(it) }
                                    searchResultAdapter?.notifyDataSetChanged()
                                    view.hidePageLoad()
                                    isRecyclerLoading = false
                                }
                            }, { throwable ->
                                ifViewAttached { view: ZephyrrSearchView ->
                                    //todo: revisit erroring the whole page on this.  Just error bottom
                                    view.showError(throwable, false)
                                }
                            })
            compositeDisposable.add(disposable)
        } else {
            ifViewAttached { view: ZephyrrSearchView ->
                view.hidePageLoad()
                view.showMaxPages()
                isRecyclerLoading = false
                loadedMaxPages = true
            }
        }
    }

    fun fetchPosterArt(cardItemPosterView: ImageView, item: SearchResultsItem) {
        Glide.with(cardItemPosterView).clear(cardItemPosterView)
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
                    .apply {
                        RequestOptions()
                                .placeholder(ColorDrawable(Color.DKGRAY))
                                .fallback(ColorDrawable(Color.DKGRAY))
                                .centerCrop()
                    }
                    .into(cardItemPosterView)
        }
    }

    fun processReleaseDate(releaseDate: String): String {
        return if (releaseDate.isNotBlank()) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(releaseDate)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            outputFormat.format(date)
        } else {
            ""
        }
    }

    fun performSearch(query: String) {
        if (searchResultsList.isEmpty()) {
            lastQuery = query
            searchResultsPageNumber = 1
            val disposable =
                    TMDBService.querySearchMulti(lastQuery!!, searchResultsPageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ response ->
                                ifViewAttached { view: ZephyrrSearchView ->
                                    if (response.totalResults == 0) {
                                        view.showEmptyState(lastQuery)
                                    } else {
                                        response.results?.map { searchResultAdapter?.searchResults?.add(it) }
                                        searchResultAdapter?.notifyDataSetChanged()
                                        view.showResults()
                                        totalAvailablePages = response.totalPages ?: -1
                                    }
                                }
                            }, { throwable ->
                                ifViewAttached { view: ZephyrrSearchView ->
                                    view.showError(throwable, false)
                                }
                            })
            compositeDisposable.add(disposable)
        } else {
            ifViewAttached { view: ZephyrrSearchView ->
                searchResultAdapter?.searchResults = searchResultsList
                searchResultAdapter?.notifyDataSetChanged()
                view.showResults()
            }
        }
    }

    fun onCardSelected(position: Int) {
        ifViewAttached { view: ZephyrrSearchView ->
            view.showDetail(Bundle().apply {
                putInt("MovieID", position)
            })
        }
    }

    override fun detachView() {
        super.detachView()
        Timber.d("Detach view")
        ifViewAttached { view: ZephyrrSearchView ->
            view.hidePageLoad()
            view.hideMaxPages()
        }
        //Copy last good list or empty. Maybe log if empty
        searchResultsList = searchResultAdapter?.searchResults?.toMutableList() ?: mutableListOf()
    }

    override fun destroy() {
        super.destroy()
        Timber.d("destroy called, disposables disposed of")
        compositeDisposable.dispose()
    }
}