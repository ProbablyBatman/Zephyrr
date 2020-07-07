package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.snackbar.Snackbar
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.LandingAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.state.LandingState
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.shouldShowError
import greenberg.moviedbshell.state.shouldShowLoading
import greenberg.moviedbshell.viewmodel.LandingViewModel
import timber.log.Timber

class LandingFragment  : BaseFragment() {

    //TODO: INVESTIGATE REPLACING THIS WITH LATEINIT VAR INJECT
    val landingViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.landingViewModelFactory
    }

    private val viewModel: LandingViewModel by fragmentViewModel()

    private lateinit var recentlyReleasedRecycler: RecyclerView
    private lateinit var recentlyReleasedLayoutManager: LinearLayoutManager
    private lateinit var recentlyReleasedAdapter: LandingAdapter
    private lateinit var popularMovieRecycler: RecyclerView
    private lateinit var popularMovieLayoutManager: LinearLayoutManager
    private lateinit var popularMovieAdapter: LandingAdapter
    private lateinit var soonTMRecycler: RecyclerView
    private lateinit var soonTMLayoutManager: LinearLayoutManager
    private lateinit var soonTMAdapter: LandingAdapter
    private lateinit var recentlyReleasedSeeAllButton: ImageView
    private lateinit var recentlyReleasedContainer: View
    private lateinit var popularSeeAllButton: ImageView
    private lateinit var popularContainer: View
    private lateinit var soonTMSeeAllButton: ImageView
    private lateinit var soonTMContainer: View
    private lateinit var progressBar: ProgressBar
    private lateinit var contentContainer: ScrollView
    private var errorSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.landing_page_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recentlyReleasedRecycler = view.findViewById(R.id.recently_released_recycler)
        popularMovieRecycler = view.findViewById(R.id.popular_movie_recycler)
        soonTMRecycler = view.findViewById(R.id.soon_tm_recycler)

        recentlyReleasedLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        popularMovieLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        soonTMLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recentlyReleasedAdapter = LandingAdapter(posterClickListener = this::onClickListener)
        popularMovieAdapter = LandingAdapter(posterClickListener = this::onClickListener)
        soonTMAdapter = LandingAdapter(posterClickListener = this::onClickListener)

        recentlyReleasedRecycler.adapter = recentlyReleasedAdapter
        recentlyReleasedRecycler.layoutManager = recentlyReleasedLayoutManager

        popularMovieRecycler.adapter = popularMovieAdapter
        popularMovieRecycler.layoutManager = popularMovieLayoutManager

        soonTMRecycler.adapter = soonTMAdapter
        soonTMRecycler.layoutManager = soonTMLayoutManager

        contentContainer = view.findViewById(R.id.content_container)
        progressBar = view.findViewById(R.id.landing_page_progress_bar)
        recentlyReleasedContainer = view.findViewById(R.id.recently_released_container)
        recentlyReleasedSeeAllButton = view.findViewById(R.id.recently_released_see_all_button)
        recentlyReleasedSeeAllButton.setOnClickListener {
            navigate(R.id.action_landingFragment_to_recentlyReleasedFragment)
        }
        popularContainer = view.findViewById(R.id.popular_movie_container)
        popularSeeAllButton = view.findViewById(R.id.popular_see_all_button)
        popularSeeAllButton.setOnClickListener {
            navigate(R.id.action_landingFragment_to_popularMovieFragment)
        }
        soonTMContainer = view.findViewById(R.id.soon_tm_container)
        soonTMSeeAllButton = view.findViewById(R.id.soon_tm_see_all_button)
        soonTMSeeAllButton.setOnClickListener {
            navigate(R.id.action_landingFragment_to_soonTMFragment)
        }
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            when {
                state.shouldShowError() -> {
                    hideLoading()
                    hideRows()
                    showError()
                }
                state.shouldShowLoading() -> {
                    hideError()
                    hideRows()
                    showLoading()
                }
                else -> {
                    hideError()
                    hideLoading()
                    showRows()
                    showMovies(state)
                }
            }
        }
    }

    private fun showMovies(state: LandingState) {
        recentlyReleasedAdapter.items = state.landingItem()?.recentlyReleasedItems.orEmpty()
        popularMovieAdapter.items = state.landingItem()?.popularMovieItems.orEmpty()
        soonTMAdapter.items = state.landingItem()?.soonTMItems.orEmpty()
        recentlyReleasedAdapter.notifyDataSetChanged()
        popularMovieAdapter.notifyDataSetChanged()
        soonTMAdapter.notifyDataSetChanged()
    }

    private fun showError() {
        errorSnackbar =
            Snackbar.make(contentContainer, getString(R.string.generic_error_text), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry)) {
                    errorSnackbar?.dismiss()
                    viewModel.fetchLandingResults()
                }
        errorSnackbar?.show()
    }

    private fun hideError() {
        errorSnackbar?.dismiss()
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        contentContainer.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        contentContainer.visibility = View.VISIBLE
    }

    private fun hideRows() {
        recentlyReleasedContainer.visibility = View.GONE
        popularContainer.visibility = View.GONE
        soonTMContainer.visibility = View.GONE
    }

    private fun showRows() {
        recentlyReleasedContainer.visibility = View.VISIBLE
        popularContainer.visibility = View.VISIBLE
        soonTMContainer.visibility = View.VISIBLE
    }

    private fun onClickListener(movieId: Int) {
        navigate(
            R.id.action_landingFragment_to_movieDetailFragment,
            MovieDetailArgs(movieId)
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}