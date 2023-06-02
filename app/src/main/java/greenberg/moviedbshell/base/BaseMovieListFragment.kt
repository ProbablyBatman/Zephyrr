package greenberg.moviedbshell.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.MovieListAdapter
import greenberg.moviedbshell.state.base.BaseMovieListState
import greenberg.moviedbshell.viewmodel.base.BaseMovieListViewModel
import javax.inject.Inject

abstract class BaseMovieListFragment<T : BaseMovieListViewModel<S>, S : BaseMovieListState> : BaseFragment() {
    @Inject
    lateinit var gridListToggleState: () -> String

    abstract val viewModel: BaseMovieListViewModel<S>

    private lateinit var movieRecycler: RecyclerView
    private lateinit var movieListAdapter: MovieListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var progressBar: ProgressBar
    protected lateinit var title: TextView
    private lateinit var gridListHolder: ViewSwitcher
    private lateinit var gridToggle: ImageView
    private lateinit var listToggle: ImageView
    private var loadingSnackbar: Snackbar? = null
    private var maxPagesSnackbar: Snackbar? = null
    private var errorSnackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieRecycler = view.findViewById(R.id.movie_list_paged_recycler)
        movieListAdapter = MovieListAdapter(onClickListener = this::onClickListener)
        log("toggle is $gridListToggleState")
        gridLayoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        gridListHolder = view.findViewById(R.id.movie_grid_list_holder)
        gridToggle = view.findViewById(R.id.movie_grid_toggle)
        listToggle = view.findViewById(R.id.movie_list_toggle)
        if (gridListToggleState.invoke() == ZephyrrApplication.GRID_LIST_GRID_VALUE) {
            movieListAdapter.updateViewType(MovieListAdapter.ViewType.VIEW_TYPE_GRID)
            movieRecycler.layoutManager = gridLayoutManager
            // By default, grid shown first, just cycle to list if we're on grid already
            gridListHolder.showNext()
//            movieRecycler.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.recycler_grid_anim_from_bottom)
            movieRecycler.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.recycler_list_fall_down_anim)
        } else {
            movieListAdapter.updateViewType(MovieListAdapter.ViewType.VIEW_TYPE_LIST)
            movieRecycler.layoutManager = linearLayoutManager
            movieRecycler.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.recycler_list_fall_down_anim)
        }
        movieRecycler.adapter = movieListAdapter
        // TODO: revisit this because it still calls multiple pages while scrolling
        movieRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                when (movieListAdapter.currentViewType) {
                    MovieListAdapter.ViewType.VIEW_TYPE_GRID -> {
                        if (gridLayoutManager.findLastVisibleItemPosition() == gridLayoutManager.itemCount - 1) {
                            viewModel.fetchMovies()
                        }
                    }
                    MovieListAdapter.ViewType.VIEW_TYPE_LIST -> {
                        if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.itemCount - 1) {
                            viewModel.fetchMovies()
                        }
                    }
                }
            }
        })
        progressBar = view.findViewById(R.id.movie_list_progress_bar)
        title = view.findViewById(R.id.movie_list_page_title)
        gridListHolder.setOnClickListener {
            (activity?.application as ZephyrrApplication).toggleGridListView()
            gridListHolder.showNext()
            when (movieListAdapter.switchViewType()) {
                MovieListAdapter.ViewType.VIEW_TYPE_GRID -> {
                    movieRecycler.layoutManager = gridLayoutManager
                    rerunAnim(true)
                }
                MovieListAdapter.ViewType.VIEW_TYPE_LIST -> {
                    movieRecycler.layoutManager = linearLayoutManager
                    rerunAnim(false)
                }
            }
        }

        registerObservers()
    }

    abstract fun registerObservers()

    abstract fun updateMovieList(state: BaseMovieListState)

    private fun rerunAnim(isGrid: Boolean) {
        // TODO: why are these the same
        movieRecycler.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), if (isGrid) {
            R.anim.recycler_list_fall_down_anim
        } else {
            R.anim.recycler_list_fall_down_anim
        })
        movieListAdapter.notifyItemRangeChanged(0, movieListAdapter.itemCount)
        movieRecycler.scheduleLayoutAnimation()
    }

    // TODO: why did I do this
    override fun onPause() {
        super.onPause()
        hideMovies()
        hidePageLoad()
        hideLoading()
        hideError()
        hideMaxPages()
    }

    protected fun hideMaxPages() {
        log("Hide max pages")
        maxPagesSnackbar?.dismiss()
    }

    protected fun showMaxPages() {
        log("Show max pages")
        maxPagesSnackbar = Snackbar.make(movieRecycler, getString(R.string.generic_max_pages_text), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.dismiss)) { maxPagesSnackbar?.dismiss() }
        if (maxPagesSnackbar?.isShown == false) {
            maxPagesSnackbar?.show()
        }
    }

    protected fun hideError() {
        log("Hide error")
        errorSnackbar?.dismiss()
    }

    protected fun showError(throwable: Throwable) {
        log("Showing Error")
        log(throwable)
        errorSnackbar = Snackbar.make(movieRecycler, getString(R.string.generic_error_text), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) {
                errorSnackbar?.dismiss()
                viewModel.fetchMovies()
            }
        errorSnackbar?.show()
    }

    protected fun hideLoading() {
        log("Hide loading")
        progressBar.visibility = View.GONE
    }

    protected fun showLoading() {
        log("Show loading")
        progressBar.visibility = View.VISIBLE
    }

    protected fun hidePageLoad() {
        log("Hide page load")
        loadingSnackbar?.dismiss()
    }

    protected fun showPageLoad() {
        log("Show page load")
        loadingSnackbar = Snackbar.make(movieRecycler, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
        loadingSnackbar?.show()
    }

    protected fun showMovies(state: BaseMovieListState) {
        log("Showing movies")
        title.visibility = View.VISIBLE
        movieRecycler.visibility = View.VISIBLE
        movieListAdapter.items = state.movieList
        movieListAdapter.notifyItemRangeChanged(0, movieListAdapter.itemCount)
    }

    protected fun hideMovies() {
        log("Hide movies")
        movieRecycler.visibility = View.GONE
        title.visibility = View.GONE
    }

    protected abstract fun onClickListener(movieId: Int)
}
