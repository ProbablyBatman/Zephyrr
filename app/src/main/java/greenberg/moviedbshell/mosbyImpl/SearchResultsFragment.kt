package greenberg.moviedbshell.mosbyImpl

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import greenberg.moviedbshell.models.SearchModels.SearchResultsItem
import greenberg.moviedbshell.R
import greenberg.moviedbshell.viewHolders.SearchResultsAdapter
import timber.log.Timber

class SearchResultsFragment :
        MvpFragment<ZephyrrSearchView, SearchPresenter>(),
        ZephyrrSearchView,
        SwipeRefreshLayout.OnRefreshListener {

    private lateinit var query: String

    private var searchActionBar: Toolbar? = null
    private var searchResultsRecycler: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var searchResultsAdapter: SearchResultsAdapter? = null
    private var searchResultsRefresher: SwipeRefreshLayout? = null
    private var searchLoadingBar: ProgressBar? = null
    private var loadingSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            query = arguments?.get("Query") as String
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.multi_search_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //searchActionBar = activity?.findViewById(R.id.popular_movie_toolbar)
        //searchActionBar?.title = getString(R.string.app_name)

        searchResultsRecycler = view.findViewById(R.id.search_results_recycler)
        searchResultsRefresher = view.findViewById(R.id.search_results_refresher)
        searchLoadingBar = view.findViewById(R.id.search_results_progress_bar)
        searchResultsRefresher?.setOnRefreshListener(this)

        linearLayoutManager = LinearLayoutManager(activity)
        searchResultsRecycler?.layoutManager = linearLayoutManager
        searchResultsAdapter = SearchResultsAdapter(presenter = presenter)
        searchResultsRecycler?.adapter = searchResultsAdapter

        presenter.initRecyclerPagination(searchResultsRecycler)

        showLoading(false)
    }

    override fun createPresenter(): SearchPresenter = presenter ?: SearchPresenter()

    override fun showLoading(pullToRefresh: Boolean) {
        Timber.d("Show Loading")
        searchResultsRefresher?.visibility = View.GONE
        searchResultsRecycler?.visibility = View.GONE
        searchLoadingBar?.visibility = View.VISIBLE
        presenter.performSearch(query)
    }

    override fun setResults(items: List<SearchResultsItem?>) {
        Timber.d("Setting results")
        searchResultsAdapter?.searchResults = items.toMutableList()
        searchResultsAdapter?.notifyDataSetChanged()
    }

    override fun addResults(items: List<SearchResultsItem?>) {
        Timber.d("Adding results")
        items.map { searchResultsAdapter?.searchResults?.add(it) }
        searchResultsAdapter?.notifyDataSetChanged()
    }

    override fun showResults() {
        Timber.d("Adding results")
        searchResultsRefresher?.isRefreshing = false
        searchLoadingBar?.visibility = View.GONE
        searchResultsRefresher?.visibility = View.VISIBLE
        searchResultsRecycler?.visibility = View.VISIBLE
    }

    override fun showError(throwable: Throwable, pullToRefresh: Boolean) {
        Timber.d("Showing error")
        searchResultsRefresher?.isRefreshing = false
    }

    override fun onRefresh() {
        Timber.d("On Refresh")
        //TODO: perhaps revisit how this is done and make the presenter do it instead
        showLoading(true)
        presenter.refreshView(searchResultsAdapter)
    }

    override fun showPageLoad() {
        Timber.d("Showing page load")
        loadingSnackbar = searchResultsRecycler?.let {
            Snackbar.make(it, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
        }
        loadingSnackbar?.show()
    }

    override fun hidePageLoad() {
        loadingSnackbar?.dismiss()
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }

    override fun onPause() {
        Timber.d("onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.d("onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
    }

    companion object {
        @JvmField val TAG: String = SearchResultsFragment::class.java.simpleName
    }

}
