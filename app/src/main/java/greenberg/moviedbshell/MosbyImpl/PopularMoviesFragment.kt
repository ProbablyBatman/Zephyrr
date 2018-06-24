package greenberg.moviedbshell.MosbyImpl

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import greenberg.moviedbshell.Models.PopularMoviesModels.PopularMovieResultsItem
import greenberg.moviedbshell.ViewHolders.PopularMovieAdapter
import greenberg.moviedbshell.R

class PopularMoviesFragment :
        MvpFragment<PopularMoviesView, PopularMoviesPresenter>(),
        PopularMoviesView,
        SwipeRefreshLayout.OnRefreshListener {

    private var popularMovieActionBar: Toolbar? = null
    private var popularMovieRecycler: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var popularMovieAdapter: PopularMovieAdapter? = null
    private var popularMovieRefresher: SwipeRefreshLayout? = null
    private var popularMovieLoadingBar: ProgressBar? = null
    private var loadingSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        //TODO: look into proper context for this; i.e. application or base
        linearLayoutManager = LinearLayoutManager(activity)
        popularMovieRecycler?.layoutManager = linearLayoutManager
        //TODO: revisit this initialization
        popularMovieAdapter = PopularMovieAdapter(popularMoviesPresenter = presenter)
        popularMovieRecycler?.adapter = popularMovieAdapter

        presenter.initRecyclerPagination(popularMovieRecycler)
    }

    override fun createPresenter(): PopularMoviesPresenter {
        if (presenter == null) {
            presenter = PopularMoviesPresenter()
        }

        return presenter
    }

    override fun showLoading(pullToRefresh: Boolean) {
        Log.w("Testing", "Show Loading")
        popularMovieRefresher?.visibility = View.GONE
        popularMovieRecycler?.visibility = View.GONE
        popularMovieLoadingBar?.visibility = View.VISIBLE
        presenter.loadPopularMovies(pullToRefresh)
    }

    override fun setMovies(items: List<PopularMovieResultsItem?>) {
        Log.w("Testing", "Setting Movies")
        popularMovieAdapter?.popularMovieList = items.toMutableList()
        popularMovieAdapter?.notifyDataSetChanged()
    }

    override fun addMovies(items: List<PopularMovieResultsItem?>) {
        Log.w("Testing", "Adding movies")
        items.map { popularMovieAdapter?.popularMovieList?.add(it) }
        popularMovieAdapter?.notifyDataSetChanged()
    }

    override fun showMovies() {
        Log.w("Testing", "Showing Movies")
        popularMovieRefresher?.isRefreshing = false
        popularMovieLoadingBar?.visibility = View.GONE
        popularMovieRefresher?.visibility = View.VISIBLE
        popularMovieRecycler?.visibility = View.VISIBLE
    }

    override fun showError(throwable: Throwable, pullToRefresh: Boolean) {
        Log.w("Testing", "Showing Error")
        popularMovieRefresher?.isRefreshing = false
    }

    override fun onRefresh() {
        Log.w("Testing", "On Refresh")
        presenter.refreshPage()
    }

    override fun showPageLoad() {
        Log.w("Testing", "showing page load")
        loadingSnackbar = popularMovieRecycler?.let {
            Snackbar.make(it, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
        }
        loadingSnackbar?.show()
    }

    override fun hidePageLoad() {
        Log.w("Hiding", "showing page load")
        loadingSnackbar?.dismiss()
    }

    override fun showDetail(fragment: MovieDetailFragment) {
        activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(MovieDetailFragment.TAG)
                ?.commit()
    }

    companion object {
        @JvmField val TAG: String = PopularMoviesFragment::class.java.simpleName
    }

}
