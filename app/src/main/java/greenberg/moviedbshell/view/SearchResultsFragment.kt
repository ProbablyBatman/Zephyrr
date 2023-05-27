package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.SearchResultsAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.PersonDetailArgs
import greenberg.moviedbshell.state.SearchResultsState
import greenberg.moviedbshell.state.TvDetailArgs
import greenberg.moviedbshell.viewmodel.SearchResultsViewModel
import timber.log.Timber

class SearchResultsFragment : BaseFragment() {

    val searchResultsViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.searchResultsViewModelFactory
    }

//    private val viewModel: SearchResultsViewModel by fragmentViewModel()

    private var navController: NavController? = null

    private var searchResultsRecycler: RecyclerView? = null
    private lateinit var linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager
    private var searchResultsAdapter: SearchResultsAdapter? = null
    private var searchLoadingBar: ProgressBar? = null
    private var loadingSnackbar: Snackbar? = null
    private var zephyrrSearchView: SearchView? = null
    private var maxPagesSnackbar: Snackbar? = null
    private var emptyStateText: TextView? = null
    private var errorTextView: TextView? = null
    private var errorRetryButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
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
        errorTextView = view.findViewById(R.id.search_error)
        errorRetryButton = view.findViewById(R.id.search_error_retry_button)

        linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        searchResultsRecycler?.layoutManager = linearLayoutManager
        searchResultsAdapter = SearchResultsAdapter(onClickListener = this::onClickListener)
        searchResultsRecycler?.adapter = searchResultsAdapter
        searchResultsRecycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.itemCount - 1) {
//                    viewModel.fetchSearchResults()
                }
            }
        })

        // TODO: maybe change the title of the action bar to show the last performed search
        navController = findNavController()
    }

    private fun showLoading() {
        Timber.d("Show Loading")
        showLoadingBar()
        hideResultsView()
        hideErrorState()
    }

    private fun showResults(state: SearchResultsState) {
        Timber.d("Showing results")
        if (state.searchResults.isEmpty()) {
            showEmptyState(state.query)
        } else {
            searchResultsAdapter?.searchResults = state.searchResults
            searchResultsAdapter?.notifyDataSetChanged()
        }
        hideLoadingBar()
        hidePageLoad()
        showResultsView()
    }

    private fun showError(throwable: Throwable) {
        Timber.d("Showing error")
        Timber.e(throwable)
        hideLoadingBar()
        hideResultsView()
        showErrorState()
        errorRetryButton?.setOnClickListener {
//            viewModel.fetchSearchResults()
            hideErrorState()
        }
    }

    private fun showPageLoad() {
        Timber.d("Showing page load")
        loadingSnackbar = searchResultsRecycler?.let {
            Snackbar.make(it, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
        }
        if (loadingSnackbar?.isShown == false) {
            loadingSnackbar?.show()
        }
    }

    private fun hidePageLoad() {
        Timber.d("Hide page load")
        loadingSnackbar?.dismiss()
    }

    private fun showMaxPages() {
        Timber.d("Show max pages")
        maxPagesSnackbar = searchResultsRecycler?.let { view ->
            Snackbar.make(view, getString(R.string.generic_max_pages_text), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.dismiss)) { maxPagesSnackbar?.dismiss() }
        }
        if (maxPagesSnackbar?.isShown == false) {
            maxPagesSnackbar?.show()
        }
    }

    private fun hideMaxPages() {
        Timber.d("Hide max pages")
        maxPagesSnackbar?.dismiss()
    }

    private fun showEmptyState(lastQuery: String?) {
        Timber.d("Showing empty state for $lastQuery")
        // TODO: look to get rid of storing the query in the view
        emptyStateText?.text = getString(R.string.empty_state_search_text, lastQuery)
        hideLoadingBar()
        emptyStateText?.visibility = View.VISIBLE
    }

    private fun showLoadingBar() {
        searchLoadingBar?.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        searchLoadingBar?.visibility = View.GONE
    }

    private fun hideResultsView() {
        searchResultsRecycler?.visibility = View.GONE
    }

    private fun showResultsView() {
        searchResultsRecycler?.visibility = View.VISIBLE
    }

    private fun hideErrorState() {
        errorTextView?.visibility = View.GONE
        errorRetryButton?.visibility = View.GONE
    }

    private fun showErrorState() {
        errorTextView?.visibility = View.VISIBLE
        errorRetryButton?.visibility = View.VISIBLE
    }

//    override fun invalidate() {
//        withState(viewModel) { state ->
//            when (state.searchResultsResponse) {
//                Uninitialized -> Timber.d("uninitialized")
//                is Loading -> {
//                    hideMaxPages()
//                    if (state.pageNumber > 1) {
//                        showPageLoad()
//                    } else {
//                        showLoading()
//                    }
//                }
//                is Success -> {
//                    if (state.totalPages == state.pageNumber) {
//                        showMaxPages()
//                    } else {
//                        showResults(state)
//                    }
//                }
//                is Fail -> {
//                    showError(state.searchResultsResponse.error)
//                }
//            }
//        }
//    }

    private fun onClickListener(itemId: Int, mediaType: String) {
        when (mediaType) {
            MediaType.MOVIE -> {
                navigate(
                    R.id.action_searchResultsFragment_to_movieDetailFragment,
                    MovieDetailArgs(itemId)
                )
            }
            MediaType.TV -> {
                navigate(
                    R.id.action_searchResultsFragment_to_tvDetailFragment,
                    TvDetailArgs(itemId)
                )
            }
            MediaType.PERSON -> {
                navigate(
                    R.id.action_searchResultsFragment_to_personDetailFragment,
                    PersonDetailArgs(itemId)
                )
            }
            MediaType.UNKNOWN -> {
                // TODO: figure this one out
                // no-op?
            }
        }
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }

    companion object {
        @JvmField
        val TAG: String = SearchResultsFragment::class.java.simpleName
    }
}
