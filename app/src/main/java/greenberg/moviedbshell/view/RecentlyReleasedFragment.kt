package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.snackbar.Snackbar
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.MovieListAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.RecentlyReleasedState
import greenberg.moviedbshell.viewmodel.RecentlyReleasedViewModel
import timber.log.Timber
import javax.inject.Inject

class RecentlyReleasedFragment : BaseFragment() {

    @Inject
    lateinit var gridListToggleState: () -> String

    val recentlyReleasedViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.recentlyReleasedViewModelFactory
    }

    private val viewModel: RecentlyReleasedViewModel by fragmentViewModel()

    private lateinit var recentlyReleasedRecycler: RecyclerView
    private lateinit var recentlyReleasedAdapter: MovieListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var title: TextView
    private lateinit var gridListToggle: ImageView
    private var loadingSnackbar: Snackbar? = null
    private var maxPagesSnackbar: Snackbar? = null
    private var errorSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        (activity?.application as ZephyrrApplication).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recently_released_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recentlyReleasedRecycler = view.findViewById(R.id.recently_released_paged_recycler)
        recentlyReleasedAdapter = MovieListAdapter(onClickListener = this::onClickListener)
        log("toggle is $gridListToggleState")
        gridLayoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        if (gridListToggleState.invoke() == ZephyrrApplication.GRID_LIST_GRID_VALUE) {
            recentlyReleasedAdapter.updateViewType(MovieListAdapter.ViewType.VIEW_TYPE_GRID)
            recentlyReleasedRecycler.layoutManager = gridLayoutManager
        } else {
            recentlyReleasedAdapter.updateViewType(MovieListAdapter.ViewType.VIEW_TYPE_LIST)
            recentlyReleasedRecycler.layoutManager = linearLayoutManager
        }
        recentlyReleasedRecycler.adapter = recentlyReleasedAdapter
        // TODO: revisit this because it still calls multiple pages while scrolling
        recentlyReleasedRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                when (recentlyReleasedAdapter.currentViewType) {
                    MovieListAdapter.ViewType.VIEW_TYPE_GRID -> {
                        if (gridLayoutManager.findLastVisibleItemPosition() == gridLayoutManager.itemCount - 1) {
                            viewModel.fetchRecentlyReleased()
                        }
                    }
                    MovieListAdapter.ViewType.VIEW_TYPE_LIST -> {
                        if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.itemCount - 1) {
                            viewModel.fetchRecentlyReleased()
                        }
                    }
                }
            }
        })

        progressBar = view.findViewById(R.id.recently_released_progress_bar)
        title = view.findViewById(R.id.recently_released_page_title)
        gridListToggle = view.findViewById(R.id.movie_grid_list_toggle)
        gridListToggle.setOnClickListener {
            (activity?.application as ZephyrrApplication).toggleGridListView()
            log("${gridListToggleState.invoke()} value is this")
            when (recentlyReleasedAdapter.switchViewType()) {
                MovieListAdapter.ViewType.VIEW_TYPE_GRID -> recentlyReleasedRecycler.layoutManager = gridLayoutManager
                MovieListAdapter.ViewType.VIEW_TYPE_LIST -> recentlyReleasedRecycler.layoutManager = linearLayoutManager
            }
        }

        log("$gridListToggleState value is this")
        viewModel.subscribe { log("State's page number is ${it.pageNumber}")}
    }

    private fun hideMaxPages() {
        log("Hide max pages")
        maxPagesSnackbar?.dismiss()
    }

    private fun showMaxPages() {
        log("Show max pages")
        maxPagesSnackbar = Snackbar.make(recentlyReleasedRecycler, getString(R.string.generic_max_pages_text), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.dismiss)) { maxPagesSnackbar?.dismiss() }
        if (maxPagesSnackbar?.isShown == false) {
            maxPagesSnackbar?.show()
        }
    }

    private fun hideError() {
        errorSnackbar?.dismiss()
    }

    private fun showError(throwable: Throwable) {
        log("Showing Error")
        Timber.e(throwable)
        errorSnackbar = Snackbar.make(recentlyReleasedRecycler, getString(R.string.generic_error_text), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) {
                errorSnackbar?.dismiss()
                viewModel.fetchRecentlyReleased()
            }
        errorSnackbar?.show()
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hidePageLoad() {
        log("Hide page load")
        loadingSnackbar?.dismiss()
    }

    private fun showPageLoad() {
        log("Show page load")
        loadingSnackbar = Snackbar.make(recentlyReleasedRecycler, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
        loadingSnackbar?.show()
    }

    private fun showMovies(state: RecentlyReleasedState) {
        log("Showing movies")
        title.visibility = View.VISIBLE
        recentlyReleasedRecycler.visibility = View.VISIBLE
        recentlyReleasedAdapter.items = state.recentlyReleasedMovies
        recentlyReleasedAdapter.notifyDataSetChanged()
    }

    private fun hideMovies() {
        recentlyReleasedRecycler.visibility = View.GONE
        title.visibility = View.GONE
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            if (state.recentlyReleasedResponse.invoke()?.totalPages == state.pageNumber) {
                showMaxPages()
            }

            when (state.recentlyReleasedResponse) {
                Uninitialized -> log("uninitialized")
                is Loading -> {
                    log("Loading")
                    hideError()
                    hideMaxPages()
                    if (state.pageNumber < 1) {
                        hideMovies()
                        showLoading()
                    } else {
                        showPageLoad()
                    }
                }
                is Success -> {
                    log("Success")
                    hidePageLoad()
                    hideMaxPages()
                    hideLoading()
                    showMovies(state)
                }
                is Fail -> {
                    log("Fail")
                    hideMovies()
                    showError(state.recentlyReleasedResponse.error)
                }
            }
        }
    }

    private fun onClickListener(movieId: Int) {
        navigate(
            R.id.action_recentlyReleasedFragment_to_movieDetailFragment,
            MovieDetailArgs(movieId)
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }
}