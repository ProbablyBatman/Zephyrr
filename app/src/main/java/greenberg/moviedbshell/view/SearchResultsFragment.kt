package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.SearchResultsAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.extensions.extractArguments
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.ui.PersonItem
import greenberg.moviedbshell.models.ui.PreviewItem
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.PersonDetailArgs
import greenberg.moviedbshell.state.SearchResultsArgs
import greenberg.moviedbshell.state.SearchResultsState
import greenberg.moviedbshell.state.TvDetailArgs
import greenberg.moviedbshell.viewmodel.MultiSearchViewModel
import greenberg.moviedbshell.viewmodel.SearchResultsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SearchResultsFragment : BaseFragment() {

    @Inject
    lateinit var searchResultsViewModelFactory: SearchResultsViewModel.Factory

    @Inject
    lateinit var multiSearchViewModelFactory: MultiSearchViewModel.Factory

    private val viewModel: SearchResultsViewModel by viewModels {
        SearchResultsViewModel.provideFactory(
            searchResultsViewModelFactory,
            arguments.extractArguments<SearchResultsArgs>(PAGE_ARGS)?.query ?: "",
            Dispatchers.IO,
        )
    }

    // Essentially this gets a singleton instance of the viewmodel since this is a single activity app
    // TODO: Not sure this is the right way to do this at all
    private val multiSearchViewModel: MultiSearchViewModel by navGraphViewModels(R.id.nav_graph) {
        MultiSearchViewModel.provideFactory(
            multiSearchViewModelFactory,
            Dispatchers.IO,
        )
    }

    private var usingMultiSearch = false

    private var navController: NavController? = null

    private lateinit var searchResultsRecycler: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var searchResultsAdapter: SearchResultsAdapter
    private lateinit var searchLoadingBar: ProgressBar
    private lateinit var searchQueryDisplay: TextView
    private var loadingSnackbar: Snackbar? = null
    private lateinit var zephyrrSearchView: SearchView
    private var maxPagesSnackbar: Snackbar? = null
    private lateinit var emptyStateText: TextView
    private lateinit var errorTextView: TextView
    private lateinit var errorRetryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zephyrr_search_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        zephyrrSearchView = view.findViewById(R.id.search_results_fragment)

        searchQueryDisplay = view.findViewById(R.id.search_query_display)
        searchResultsRecycler = view.findViewById(R.id.search_results_recycler)
        searchLoadingBar = view.findViewById(R.id.search_results_progress_bar)
        emptyStateText = view.findViewById(R.id.search_empty_state_text)
        errorTextView = view.findViewById(R.id.search_error)
        errorRetryButton = view.findViewById(R.id.search_error_retry_button)

        linearLayoutManager = LinearLayoutManager(activity)
        searchResultsRecycler.layoutManager = linearLayoutManager
        searchResultsAdapter = SearchResultsAdapter(onClickListener = this::onClickListener)
        searchResultsRecycler.adapter = searchResultsAdapter
        searchResultsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.itemCount - 1) {
                    viewModel.fetchSearchResults()
                }
            }
        })

        // TODO: maybe change the title of the action bar to show the last performed search
        navController = findNavController()

        registerObservers()

        usingMultiSearch = arguments.extractArguments<SearchResultsArgs>(PAGE_ARGS)?.usingMultiSearch == true
    }

    private fun registerObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchResultState.collect {
                        updateSearchResults(it)
                    }
                }
            }
        }
    }

    private fun updateSearchResults(state: SearchResultsState) {
        when {
            state.isLoading -> {
                hideMaxPages()
                if (state.pageNumber > 1) {
                    showPageLoad()
                } else {
                    showLoading()
                }
            }
            state.error != null -> {
                showError(state.error)
            }
            else -> {
                if (state.totalPages == state.pageNumber) {
                    showMaxPages()
                } else {
                    showResults(state)
                }
            }
        }
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
            searchQueryDisplay.text = resources.getString(R.string.search_query_display_text, state.query)
            val results = if (usingMultiSearch) {
                state.searchResults.filterIsInstance<PersonItem>()
            } else {
                state.searchResults
            }
            searchResultsAdapter.searchResults = results
            // TODO: magic number?
            searchResultsAdapter.notifyItemRangeChanged(0, searchResultsAdapter.itemCount)
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
        errorRetryButton.setOnClickListener {
            viewModel.fetchSearchResults()
            hideErrorState()
        }
    }

    private fun showPageLoad() {
        Timber.d("Showing page load")
        loadingSnackbar = Snackbar.make(searchResultsRecycler, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
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
        maxPagesSnackbar = Snackbar.make(
            searchResultsRecycler,
            getString(R.string.generic_max_pages_text),
            Snackbar.LENGTH_INDEFINITE,
        )
            .setAction(getString(R.string.dismiss)) { maxPagesSnackbar?.dismiss() }
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
        emptyStateText.text = getString(R.string.empty_state_search_text, lastQuery)
        hideLoadingBar()
        emptyStateText.visibility = View.VISIBLE
    }

    private fun showLoadingBar() {
        searchLoadingBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        searchLoadingBar.visibility = View.GONE
    }

    private fun hideResultsView() {
        searchQueryDisplay.visibility = View.GONE
        searchResultsRecycler.visibility = View.GONE
    }

    private fun showResultsView() {
        searchQueryDisplay.visibility = View.VISIBLE
        searchResultsRecycler.visibility = View.VISIBLE
    }

    private fun hideErrorState() {
        errorTextView.visibility = View.GONE
        errorRetryButton.visibility = View.GONE
    }

    private fun showErrorState() {
        errorTextView.visibility = View.VISIBLE
        errorRetryButton.visibility = View.VISIBLE
    }

    private fun onClickListener(previewItem: PreviewItem) {
        val itemId = previewItem.id ?: -1
        val mediaType = previewItem.mediaType
        if (usingMultiSearch) {
            // TODO: this cast will always succeed since we're only looking up people?
            multiSearchViewModel.addQuery(previewItem as? PersonItem)
            navController?.popBackStack()
            return
        }

        when (mediaType) {
            MediaType.MOVIE -> {
                navigate(
                    R.id.action_searchResultsFragment_to_movieDetailFragment,
                    MovieDetailArgs(itemId),
                )
            }
            MediaType.TV -> {
                navigate(
                    R.id.action_searchResultsFragment_to_tvDetailFragment,
                    TvDetailArgs(itemId),
                )
            }
            MediaType.PERSON -> {
                navigate(
                    R.id.action_searchResultsFragment_to_personDetailFragment,
                    PersonDetailArgs(itemId),
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
}
