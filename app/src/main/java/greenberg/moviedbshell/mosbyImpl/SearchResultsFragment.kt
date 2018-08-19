package greenberg.moviedbshell.mosbyImpl

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.SearchModels.SearchResultsItem
import greenberg.moviedbshell.viewHolders.SearchResultsAdapter
import timber.log.Timber

class SearchResultsFragment :
        MvpFragment<ZephyrrSearchView, SearchPresenter>(),
        ZephyrrSearchView {

    private lateinit var query: String
    private var navController: NavController? = null

    private var searchResultsRecycler: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var searchResultsAdapter: SearchResultsAdapter? = null
    private var searchLoadingBar: ProgressBar? = null
    private var loadingSnackbar: Snackbar? = null
    private var zephyrrSearchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setHasOptionsMenu(false)
        query = arguments?.get("Query") as String
        navController = findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zephyrr_search_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")

        zephyrrSearchView = view.findViewById(R.id.searchResultsFragment)

        searchResultsRecycler = view.findViewById(R.id.search_results_recycler)
        searchLoadingBar = view.findViewById(R.id.search_results_progress_bar)

        linearLayoutManager = LinearLayoutManager(activity)
        searchResultsRecycler?.layoutManager = linearLayoutManager
        searchResultsAdapter = SearchResultsAdapter(presenter = presenter)
        searchResultsRecycler?.adapter = searchResultsAdapter

        presenter.initRecyclerPagination(searchResultsRecycler)
        //TODO: maybe change the title of the action bar to show the last performed search
        //noactivity?.actionBar?.title = query
        presenter.performSearch(query)
    }

    override fun createPresenter(): SearchPresenter = presenter ?: SearchPresenter()

    override fun showLoading() {
        Timber.d("Show Loading")
        searchResultsRecycler?.visibility = View.GONE
        searchLoadingBar?.visibility = View.VISIBLE
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
        searchLoadingBar?.visibility = View.GONE
        searchResultsRecycler?.visibility = View.VISIBLE
    }

    override fun showError(throwable: Throwable, pullToRefresh: Boolean) {
        Timber.d("Showing error")
    }

    override fun showPageLoad() {
        Timber.d("Showing page load")
        loadingSnackbar = searchResultsRecycler?.let {
            Snackbar.make(it, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
        }
        loadingSnackbar?.show()
    }

    override fun showDetail(bundle: Bundle) {
        navController?.navigate(R.id.action_searchResultsFragment_to_movieDetailFragment, bundle)
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
        @JvmField
        val TAG: String = SearchResultsFragment::class.java.simpleName
    }

}
