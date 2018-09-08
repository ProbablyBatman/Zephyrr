package greenberg.moviedbshell.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.presenters.PopularMoviesPresenter
import greenberg.moviedbshell.viewHolders.PopularMovieAdapter
import timber.log.Timber

class PopularMoviesFragment :
        BaseFragment<PopularMoviesView, PopularMoviesPresenter>(),
        PopularMoviesView,
        SwipeRefreshLayout.OnRefreshListener {

    private var popularMovieRecycler: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var popularMovieAdapter: PopularMovieAdapter? = null
    private var popularMovieRefresher: SwipeRefreshLayout? = null
    private var popularMovieLoadingBar: ProgressBar? = null
    private var loadingSnackbar: Snackbar? = null
    private var maxPagesSnackbar: Snackbar? = null
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
        // TODO: revisit this initialization
        popularMovieAdapter = PopularMovieAdapter(presenter = presenter)
        popularMovieRecycler?.adapter = popularMovieAdapter

        presenter.initView()
        presenter.initRecyclerPagination(popularMovieRecycler, popularMovieAdapter)
        presenter.loadPopularMoviesList(true)
        navController = findNavController()
    }

    override fun createPresenter(): PopularMoviesPresenter = presenter
            ?: (activity?.application as ZephyrrApplication).component.popularMoviesPresenter()

    override fun showLoading(pullToRefresh: Boolean) {
        Timber.d("Show Loading")
        popularMovieRefresher?.visibility = View.GONE
        popularMovieRecycler?.visibility = View.GONE
        popularMovieLoadingBar?.visibility = View.VISIBLE
    }

    override fun showMovies() {
        Timber.d("Showing Movies")
        popularMovieRefresher?.isRefreshing = false
        popularMovieLoadingBar?.visibility = View.GONE
        popularMovieRefresher?.visibility = View.VISIBLE
        popularMovieRecycler?.visibility = View.VISIBLE
    }

    override fun showError(throwable: Throwable, pullToRefresh: Boolean) {
        Timber.d("Showing Error")
        popularMovieRefresher?.isRefreshing = false
    }

    override fun onRefresh() {
        Timber.d("On Refresh")
        presenter.refreshPage()
        presenter.loadPopularMoviesList(true)
    }

    override fun showPageLoad() {
        Timber.d("Showing Page Load")
        loadingSnackbar = popularMovieRecycler?.let {
            Snackbar.make(it, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
        }
        loadingSnackbar?.show()
    }

    override fun hidePageLoad() {
        Timber.d("Showing Page Load")
        loadingSnackbar?.dismiss()
    }

    override fun showMaxPages() {
        Timber.d("Show max pages")
        maxPagesSnackbar = popularMovieRecycler?.let {
            Snackbar.make(it, getString(R.string.generic_max_pages_text), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.dismiss), { maxPagesSnackbar?.dismiss() })
        }
        if (maxPagesSnackbar?.isShown == false) {
            maxPagesSnackbar?.show()
        }
    }

    override fun hideMaxPages() {
        Timber.d("Hide max pages")
        maxPagesSnackbar?.dismiss()
    }

    override fun showDetail(bundle: Bundle) {
        navController?.navigate(R.id.action_popularMoviesFragment_to_movieDetailFragment, bundle)
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    companion object {
        @JvmField
        val TAG: String = PopularMoviesFragment::class.java.simpleName
    }
}
