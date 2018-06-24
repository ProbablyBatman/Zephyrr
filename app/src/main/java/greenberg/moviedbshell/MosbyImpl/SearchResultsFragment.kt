package greenberg.moviedbshell.MosbyImpl

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import greenberg.moviedbshell.Models.SearchModels.SearchResponse
import greenberg.moviedbshell.Models.SearchModels.SearchResultsItem
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ViewHolders.SearchResultsAdapter

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
        searchResultsAdapter = SearchResultsAdapter()
        searchResultsRecycler?.adapter = searchResultsAdapter

        presenter.initRecyclerPagination(searchResultsRecycler)

        showLoading(false)
    }

    override fun createPresenter(): SearchPresenter {
        if (presenter == null) {
            presenter = SearchPresenter()
        }

        return presenter
    }

    override fun showLoading(pullToRefresh: Boolean) {
        Log.w("Testing", "Show Loading")
        searchResultsRefresher?.visibility = View.GONE
        searchResultsRecycler?.visibility = View.GONE
        searchLoadingBar?.visibility = View.VISIBLE
        presenter.performSearch(query) // Nice!
    }

    override fun setResults(items: List<SearchResultsItem?>) {
        Log.w("Testing", "Setting results")
        searchResultsAdapter?.searchResults = items.toMutableList()
        searchResultsAdapter?.notifyDataSetChanged()
    }

    override fun addResults(items: List<SearchResultsItem?>) {
        Log.w("Testing", "Adding results")
        items.map { searchResultsAdapter?.searchResults?.add(it) }
        searchResultsAdapter?.notifyDataSetChanged()
    }

    override fun showResults() {
        Log.w("Testing", "Adding results")
        searchResultsRefresher?.isRefreshing = false
        searchLoadingBar?.visibility = View.GONE
        searchResultsRefresher?.visibility = View.VISIBLE
        searchResultsRecycler?.visibility = View.VISIBLE
    }

    override fun showError(throwable: Throwable, pullToRefresh: Boolean) {
        Log.w("Testing", "Showing error")
        searchResultsRefresher?.isRefreshing = false
    }

    override fun onRefresh() {
        Log.w("Testing", "On Refresh")
        //TODO: perhaps revisit how this is doen and make the presenter do it instead
        showLoading(true)
        searchResultsAdapter?.searchResults?.clear()
        searchResultsAdapter?.notifyDataSetChanged()
    }

    override fun showPageLoad() {
        Log.w("Testing", "Showing page load")
        loadingSnackbar = searchResultsRecycler?.let {
            Snackbar.make(it, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
        }
        loadingSnackbar?.show()
    }

    override fun hidePageLoad() {
        loadingSnackbar?.dismiss()
    }

    companion object {
        @JvmField val TAG: String = SearchResultsFragment::class.java.simpleName
    }

}
