package greenberg.moviedbshell.presenters

//import android.content.Context
//import android.os.Bundle
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import greenberg.moviedbshell.mappers.SearchResultsMapper
//import greenberg.moviedbshell.models.ui.MovieItem
//import greenberg.moviedbshell.models.ui.PreviewItem
//import greenberg.moviedbshell.models.ui.TvItem
//import greenberg.moviedbshell.processReleaseDate
//import greenberg.moviedbshell.services.TMDBService
//import greenberg.moviedbshell.view.ZephyrrSearchView
//import greenberg.moviedbshell.adapters.SearchResultsAdapter
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.CompositeDisposable
//import io.reactivex.schedulers.Schedulers
//import timber.log.Timber
//import javax.inject.Inject
//
//class SearchPresenter {
//@Inject constructor(
//    private val TMDBService: TMDBService,
//    private val context: Context,
//    private val searchResultsMapper: SearchResultsMapper
//) : MvpBasePresenter<ZephyrrSearchView>() {
//
//    private var isRecyclerLoading = false
//    // Default to getting the first page
//    private var searchResultsPageNumber = 1
//    private var totalAvailablePages = -1
//    // It is enforced that this string is at least not null and blank
//    private var lastQuery: String? = null
//    private var searchResultsList = mutableListOf<PreviewItem>()
//    private var searchResultAdapter: SearchResultsAdapter? = null
//    private var compositeDisposable = CompositeDisposable()
//    private var loadedMaxPages = false
//
//    override fun attachView(view: ZephyrrSearchView) {
//        super.attachView(view)
//        Timber.d("attachView")
//        if (searchResultsList.isEmpty()) {
//            view.showLoading()
//        }
//    }
//
//    fun initRecyclerPagination(recyclerView: RecyclerView?, adapter: SearchResultsAdapter?) {
//        searchResultAdapter = adapter
//        recyclerView?.apply {
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//
//                    val layoutManager = this@apply.layoutManager as LinearLayoutManager
//                    val visibleItemCount = layoutManager.childCount
//                    val totalItemCount = layoutManager.itemCount
//                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//
//                    if (!isRecyclerLoading &&
//                            (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
//                            firstVisibleItemPosition >= 0) {
//                        ifViewAttached { view: ZephyrrSearchView ->
//                            isRecyclerLoading = true
//                            if (!loadedMaxPages) {
//                                view.showPageLoad()
//                                fetchNextPage(lastQuery!!)
//                            }
//                        }
//                    }
//                }
//            })
//        }
//    }
//
//    // Gets next page of search/multi movies call
//    private fun fetchNextPage(query: String) {
//        if (searchResultsPageNumber < totalAvailablePages && totalAvailablePages != -1) {
//            val disposable =
//                    TMDBService.querySearchMulti(query, ++searchResultsPageNumber)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe({ response ->
//                                ifViewAttached { view: ZephyrrSearchView ->
//                                    searchResultAdapter?.searchResults?.addAll(searchResultsMapper.mapToEntity(response))
//                                    searchResultAdapter?.notifyDataSetChanged()
//                                    view.hidePageLoad()
//                                    isRecyclerLoading = false
//                                }
//                            }, { throwable ->
//                                ifViewAttached { view: ZephyrrSearchView ->
//                                    view.showError(throwable, false)
//                                }
//                            })
//            compositeDisposable.add(disposable)
//        } else {
//            ifViewAttached { view: ZephyrrSearchView ->
//                view.hidePageLoad()
//                view.showMaxPages()
//                isRecyclerLoading = false
//                loadedMaxPages = true
//            }
//        }
//    }
//
//    fun performSearch(query: String) {
//        Timber.d("Performing search: $query")
//        ifViewAttached { view: ZephyrrSearchView ->
//            view.showLoading()
//        }
//        if (searchResultsList.isEmpty()) {
//            lastQuery = query
//            searchResultsPageNumber = 1
//            val disposable =
//                    TMDBService.querySearchMulti(lastQuery!!, searchResultsPageNumber)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe({ response ->
//                                ifViewAttached { view: ZephyrrSearchView ->
//                                    if (response.totalResults == 0) {
//                                        view.showEmptyState(lastQuery)
//                                    } else {
//                                        searchResultAdapter?.searchResults?.addAll(searchResultsMapper.mapToEntity(response))
//                                        searchResultAdapter?.notifyDataSetChanged()
//                                        view.showResults()
//                                        totalAvailablePages = response.totalPages ?: -1
//                                    }
//                                }
//                            }, { throwable ->
//                                ifViewAttached { view: ZephyrrSearchView ->
//                                    view.showError(throwable, false)
//                                }
//                            })
//            compositeDisposable.add(disposable)
//        } else {
//            ifViewAttached { view: ZephyrrSearchView ->
//                searchResultAdapter?.searchResults = searchResultsList
//                searchResultAdapter?.notifyDataSetChanged()
//                view.showResults()
//            }
//        }
//    }
//
//    fun onCardSelected(cardItemId: Int, mediaType: String) {
//        ifViewAttached { view: ZephyrrSearchView ->
//            view.showDetail(Bundle().apply {
//                // TODO: update this to when
//                when (mediaType) {
//                    MEDIA_TYPE_MOVIE -> putInt("MovieID", cardItemId)
//                    MEDIA_TYPE_TV -> putInt("TvDetailID", cardItemId)
//                    MEDIA_TYPE_PERSON -> putInt("PersonID", cardItemId)
//                }
//            }, mediaType)
//        }
//    }
//
//    fun processKnownForItems(items: List<PreviewItem>): String {
//        return "Most Known for: \n" +
//                items.joinToString(separator = "\n") { item ->
//                    when (item) {
//                        is MovieItem -> {
//                            "Movie: ${item.movieTitle}, ${processReleaseDate(item.releaseDate)}"
//                        }
//                        is TvItem -> {
//                            "TV show: ${item.name}, ${processReleaseDate(item.firstAirDate)}"
//                        }
//                        else -> ""
//                    }
//                }
//    }
//
//    override fun detachView() {
//        Timber.d("Detach view")
//        ifViewAttached { view: ZephyrrSearchView ->
//            view.hidePageLoad()
//            view.hideMaxPages()
//        }
//        // Copy last good list or empty. Maybe log if empty
//        searchResultsList = searchResultAdapter?.searchResults?.toMutableList() ?: mutableListOf()
//        super.detachView()
//    }
//
//    override fun destroy() {
//        Timber.d("destroy called, disposables disposed of")
//        compositeDisposable.dispose()
//        super.destroy()
//    }
//
//}