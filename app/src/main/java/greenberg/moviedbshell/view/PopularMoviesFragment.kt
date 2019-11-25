package greenberg.moviedbshell.view

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.adapters.PopularMovieAdapter
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.PopularMovieState
import greenberg.moviedbshell.viewmodel.PopularMoviesViewModel
import timber.log.Timber

class PopularMoviesFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    val popularMoviesViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.popularViewModelFactory
    }

    private val viewModel: PopularMoviesViewModel by fragmentViewModel()

    private var popularMovieRecycler: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var popularMovieAdapter: PopularMovieAdapter? = null
    private var popularMovieRefresher: SwipeRefreshLayout? = null
    private var popularMovieLoadingBar: ProgressBar? = null
    private var loadingSnackbar: Snackbar? = null
    private var maxPagesSnackbar: Snackbar? = null
    private var errorSnackbar: Snackbar? = null
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.popular_movies_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        popularMovieRecycler = view.findViewById(R.id.popularMovieRecycler)
        popularMovieRefresher = view.findViewById(R.id.popularMovieRefresher)
        popularMovieLoadingBar = view.findViewById(R.id.popularMovieProgressBar)
        popularMovieRefresher?.setOnRefreshListener(this)

        // TODO: look into proper context for this; i.e. application or base
        linearLayoutManager = LinearLayoutManager(activity)
        popularMovieRecycler?.layoutManager = linearLayoutManager
        popularMovieAdapter = PopularMovieAdapter(onClickListener = this::onClickListener)
        popularMovieRecycler?.adapter = popularMovieAdapter
        // TODO: revisit this because it still calls multiple pages while scrolling
        popularMovieRecycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.itemCount - 1) {
                    viewModel.fetchPopularMovies()
                }
            }
        })
        navController = findNavController()
        viewModel.fetchPopularMovies()

        // TODO: why does this get hit like 30 times after scrolling one page?
        viewModel.subscribe { Timber.d("State's page number is ${it.pageNumber}") }
    }

    private fun showLoading() {
        Timber.d("Show Loading")
        popularMovieRefresher?.visibility = View.GONE
        popularMovieRecycler?.visibility = View.GONE
        popularMovieLoadingBar?.visibility = View.VISIBLE
    }

    private fun showMovies(state: PopularMovieState) {
        Timber.d("Showing Movies")
        popularMovieRefresher?.isRefreshing = false
        popularMovieLoadingBar?.visibility = View.GONE
        popularMovieRefresher?.visibility = View.VISIBLE
        popularMovieRecycler?.visibility = View.VISIBLE
        // Might have to change the popular movie list to just be latest set
        popularMovieAdapter?.popularMovieList = state.popularMovies
        popularMovieAdapter?.notifyDataSetChanged()
    }

    private fun showError(throwable: Throwable) {
        Timber.d("Showing Error")
        Timber.e(throwable)
        // hide progress bar
        popularMovieRefresher?.isRefreshing = false
        errorSnackbar = popularMovieRecycler?.let { view ->
            Snackbar.make(view, getString(R.string.generic_error_text), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry)) {
                        errorSnackbar?.dismiss()
                        viewModel.fetchPopularMovies()
                    }
        }
        errorSnackbar?.show()
    }

    private fun hideError() {
        errorSnackbar?.dismiss()
    }

    override fun onRefresh() {
        Timber.d("On Refresh")
        showLoading()
        viewModel.fetchFirstPage()
    }

    private fun showPageLoad() {
        Timber.d("Showing Page Load")
        loadingSnackbar = popularMovieRecycler?.let {
            Snackbar.make(it, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
        }
        loadingSnackbar?.show()
    }

    private fun hidePageLoad() {
        Timber.d("Showing Page Load")
        loadingSnackbar?.dismiss()
    }

    private fun showMaxPages() {
        Timber.d("Show max pages")
        maxPagesSnackbar = popularMovieRecycler?.let { view ->
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

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            // Borderline unnecessary check for max pages
            // Users would have to scroll through every movie to get here...
            if (state.popularMovieResponse.invoke()?.totalPages == state.pageNumber) {
                showMaxPages()
            }

            when (state.popularMovieResponse) {
                Uninitialized -> Timber.d("uninitialized")
                is Loading -> {
                    Timber.d("Loading")
                    hideError()
                    hideMaxPages()
                    // TODO: revisit this because this seems dumb
                    if (state.pageNumber < 1) {
                        showLoading()
                    } else {
                        showPageLoad()
                    }
                }
                is Success -> {
                    Timber.d("Success")
                    hidePageLoad()
                    hideMaxPages()
                    showMovies(state)
                }
                is Fail -> {
                    Timber.d("Fail")
                    showError(state.popularMovieResponse.error)
                }
            }
        }
    }

    private fun onClickListener(movieId: Int) {
        navigate(
                R.id.action_popularMoviesFragment_to_movieDetailFragment,
                MovieDetailArgs(movieId)
        )
    }

    companion object {
        @JvmField
        val TAG: String = PopularMoviesFragment::class.java.simpleName
    }
}
