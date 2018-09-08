package greenberg.moviedbshell.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.presenters.SearchPresenter
import greenberg.moviedbshell.viewHolders.SearchResultsAdapter
import timber.log.Timber

class SearchResultsFragment :
        BaseFragment<ZephyrrSearchView, SearchPresenter>(),
        ZephyrrSearchView {

    private lateinit var query: String
    private var navController: NavController? = null

    private var searchResultsRecycler: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var searchResultsAdapter: SearchResultsAdapter? = null
    private var searchLoadingBar: ProgressBar? = null
    private var loadingSnackbar: Snackbar? = null
    private var zephyrrSearchView: SearchView? = null
    private var maxPagesSnackbar: Snackbar? = null
    private var emptyStateText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        query = arguments?.get("Query") as String
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zephyrr_search_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zephyrrSearchView = view.findViewById(R.id.searchResultsFragment)

        searchResultsRecycler = view.findViewById(R.id.search_results_recycler)
        searchLoadingBar = view.findViewById(R.id.search_results_progress_bar)
        emptyStateText = view.findViewById(R.id.search_empty_state_text)

        linearLayoutManager = LinearLayoutManager(activity)
        searchResultsRecycler?.layoutManager = linearLayoutManager
        searchResultsAdapter = SearchResultsAdapter(presenter = presenter)
        searchResultsRecycler?.adapter = searchResultsAdapter

        presenter.initRecyclerPagination(searchResultsRecycler, searchResultsAdapter)
        // TODO: maybe change the title of the action bar to show the last performed search
        // noactivity?.actionBar?.title = query
        presenter.performSearch(query)
        navController = findNavController()
    }

    override fun createPresenter(): SearchPresenter = presenter
            ?: (activity?.application as ZephyrrApplication).component.searchPresenter()

    override fun showLoading() {
        Timber.d("Show Loading")
        searchResultsRecycler?.visibility = View.GONE
        searchLoadingBar?.visibility = View.VISIBLE
    }

    override fun showResults() {
        Timber.d("Showing results")
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
        if (loadingSnackbar?.isShown == false) {
            loadingSnackbar?.show()
        }
    }

    override fun hidePageLoad() {
        Timber.d("Hide page load")
        loadingSnackbar?.dismiss()
    }

    override fun showDetail(bundle: Bundle, mediaType: String) {
        when (mediaType) {
            SearchPresenter.MEDIA_TYPE_MOVIE ->
                navController?.navigate(R.id.action_searchResultsFragment_to_movieDetailFragment, bundle)
            SearchPresenter.MEDIA_TYPE_TV ->
                navController?.navigate(R.id.action_searchResultsFragment_to_tvDetailFragment, bundle)
            SearchPresenter.MEDIA_TYPE_PERSON ->
                navController?.navigate(R.id.action_searchResultsFragment_to_personDetailFragment, bundle)
        }
    }

    override fun showMaxPages() {
        Timber.d("Show max pages")
        maxPagesSnackbar = searchResultsRecycler?.let { view ->
            Snackbar.make(view, getString(R.string.generic_max_pages_text), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.dismiss)) { maxPagesSnackbar?.dismiss() }
        }
        if (maxPagesSnackbar?.isShown == false) {
            maxPagesSnackbar?.show()
        }
    }

    override fun hideMaxPages() {
        Timber.d("Hide max pages")
        maxPagesSnackbar?.dismiss()
    }

    override fun showEmptyState(lastQuery: String?) {
        Timber.d("Showing empty state for $lastQuery")
        // TODO: look to get rid of storing the query in the view
        emptyStateText?.text = getString(R.string.empty_state_search_text, lastQuery)
        searchLoadingBar?.visibility = View.GONE
        emptyStateText?.visibility = View.VISIBLE
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    companion object {
        @JvmField
        val TAG: String = SearchResultsFragment::class.java.simpleName
    }
}
