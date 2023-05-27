package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.LandingAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.extensions.flipVisibility
import greenberg.moviedbshell.state.LandingPageUiState
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.TvDetailArgs
import greenberg.moviedbshell.viewmodel.LandingViewModelV2
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LandingFragmentV2 : BaseFragment() {

    private val viewModel: LandingViewModelV2 by viewModels()

    private lateinit var recentlyReleasedRecycler: RecyclerView
    private lateinit var recentlyReleasedLayoutManager: LinearLayoutManager
    private lateinit var recentlyReleasedAdapter: LandingAdapter
    private lateinit var popularMovieRecycler: RecyclerView
    private lateinit var popularMovieLayoutManager: LinearLayoutManager
    private lateinit var popularMovieAdapter: LandingAdapter
    private lateinit var soonTMRecycler: RecyclerView
    private lateinit var soonTMLayoutManager: LinearLayoutManager
    private lateinit var soonTMAdapter: LandingAdapter
    private lateinit var popularTvRecycler: RecyclerView
    private lateinit var popularTvLayoutManager: LinearLayoutManager
    private lateinit var popularTvAdapter: LandingAdapter
    private lateinit var topRatedTvRecycler: RecyclerView
    private lateinit var topRatedTvLayoutManager: LinearLayoutManager
    private lateinit var topRatedAdapter: LandingAdapter
    private lateinit var recentlyReleasedSeeAllButton: Button
    private lateinit var recentlyReleasedContainer: View
    private lateinit var recentlyReleasedLoadingBar: ProgressBar
    private lateinit var popularMovieSeeAllButton: Button
    private lateinit var popularMovieContainer: View
    private lateinit var popularMovieLoadingBar: ProgressBar
    private lateinit var soonTMSeeAllButton: Button
    private lateinit var soonTMContainer: View
    private lateinit var soonTMLoadingBar: ProgressBar
    private lateinit var popularTvSeeAllButton: Button
    private lateinit var popularTvContainer: View
    private lateinit var popularTvLoadingBar: ProgressBar
    private lateinit var topRatedTvSeeAllButton: Button
    private lateinit var topRatedTvContainer: View
    private lateinit var topRatedLoadingBar: ProgressBar
    private lateinit var errorContainer: View
    private lateinit var contentContainer: ScrollView


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
        popularTvRecycler = view.findViewById(R.id.popular_tv_recycler)
        topRatedTvRecycler = view.findViewById(R.id.top_rated_tv_recycler)
        errorContainer = view.findViewById(R.id.landing_row_error_container)
        errorContainer.setOnClickListener {
            viewModel.retryLandingPageLists()
        }

        recentlyReleasedLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        popularMovieLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        soonTMLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        popularTvLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        topRatedTvLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recentlyReleasedAdapter = LandingAdapter(posterClickListener = this::movieOnClickListener)
        popularMovieAdapter = LandingAdapter(posterClickListener = this::movieOnClickListener)
        soonTMAdapter = LandingAdapter(posterClickListener = this::movieOnClickListener)
        popularTvAdapter = LandingAdapter(posterClickListener = this::tvOnClickListener)
        topRatedAdapter = LandingAdapter(posterClickListener = this::tvOnClickListener)

        recentlyReleasedRecycler.adapter = recentlyReleasedAdapter
        recentlyReleasedRecycler.layoutManager = recentlyReleasedLayoutManager

        popularMovieRecycler.adapter = popularMovieAdapter
        popularMovieRecycler.layoutManager = popularMovieLayoutManager

        soonTMRecycler.adapter = soonTMAdapter
        soonTMRecycler.layoutManager = soonTMLayoutManager

        popularTvRecycler.adapter = popularTvAdapter
        popularTvRecycler.layoutManager = popularTvLayoutManager

        topRatedTvRecycler.adapter = topRatedAdapter
        topRatedTvRecycler.layoutManager = topRatedTvLayoutManager

        contentContainer = view.findViewById(R.id.content_container)
        recentlyReleasedContainer = view.findViewById(R.id.recently_released_container)
        recentlyReleasedLoadingBar = view.findViewById(R.id.recently_released_progress_bar)
        recentlyReleasedSeeAllButton = view.findViewById(R.id.recently_released_see_all_button)
        recentlyReleasedSeeAllButton.setOnClickListener {
//            navigate(R.id.action_landingFragment_to_recentlyReleasedFragment)
        }
        popularMovieContainer = view.findViewById(R.id.popular_movie_container)
        popularMovieLoadingBar = view.findViewById(R.id.popular_movie_progress_bar)
        popularMovieSeeAllButton = view.findViewById(R.id.popular_see_all_button)
        popularMovieSeeAllButton.setOnClickListener {
//            navigate(R.id.action_landingFragment_to_popularMovieFragment)
        }
        soonTMContainer = view.findViewById(R.id.soon_tm_container)
        soonTMLoadingBar = view.findViewById(R.id.soon_tm_progress_bar)
        soonTMSeeAllButton = view.findViewById(R.id.soon_tm_see_all_button)
        soonTMSeeAllButton.setOnClickListener {
            navigate(R.id.action_landingFragment_to_soonTMFragment)
        }
        popularTvContainer = view.findViewById(R.id.popular_tv_container)
        popularTvLoadingBar = view.findViewById(R.id.popular_tv_progress_bar)
        popularTvSeeAllButton = view.findViewById(R.id.popular_tv_see_all_button)
        popularTvSeeAllButton.setOnClickListener {
            // TODO
        }
        topRatedTvContainer = view.findViewById(R.id.top_rated_tv_container)
        topRatedLoadingBar = view.findViewById(R.id.top_rated_tv_progress_bar)
        topRatedTvSeeAllButton = view.findViewById(R.id.top_rated_tv_see_all_button)
        topRatedTvSeeAllButton.setOnClickListener {
            // TODO
        }

        lifecycleScope.launch {
            viewModel.uiState
                // TODO: It might be worth revisiting whether or not I actually want this to happen every time
                // onStart is called?
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { state ->
                    when (state) {
                        is LandingPageUiState.Success -> showSuccess(state)
                        is LandingPageUiState.Failure -> showFailure(state)
                        LandingPageUiState.Loading -> showLoading()
                    }
                }
        }

    }

    fun showLoading() {
        // TODO: probably useless, but should use it to toggle loading on
    }

    private fun showSuccess(state: LandingPageUiState.Success) {
        if (errorContainer.isVisible) {
            toggleRowVisibility()
        }
        log("ssgreenb updateSuccess with $state")
        popularMovieAdapter.setItems(state.popularMovieList)
        popularMovieLoadingBar.visibility = View.GONE
        popularMovieRecycler.visibility = View.VISIBLE

        recentlyReleasedAdapter.setItems(state.recentlyReleasedMovieList)
        recentlyReleasedLoadingBar.visibility = View.GONE
        recentlyReleasedRecycler.visibility = View.VISIBLE

        soonTMAdapter.setItems(state.soonTMMovieList)
        soonTMLoadingBar.visibility = View.GONE
        soonTMRecycler.visibility = View.VISIBLE

        popularTvAdapter.setItems(state.popularTVList)
        popularTvLoadingBar.visibility = View.GONE
        popularTvRecycler.visibility = View.VISIBLE

        topRatedAdapter.setItems(state.popularTVList)
        topRatedLoadingBar.visibility = View.GONE
        topRatedTvRecycler.visibility = View.VISIBLE
    }

    private fun showFailure(state: LandingPageUiState.Failure) {
        Timber.d("ssgreenb showFailure with $state")
//        toggleRowVisibility()

        // TODO Hide all views and only have 1 retry container and button
    }

    private fun toggleRowVisibility() {
        popularMovieContainer.flipVisibility()
        recentlyReleasedContainer.flipVisibility()
        soonTMContainer.flipVisibility()
        popularTvContainer.flipVisibility()
        topRatedTvContainer.flipVisibility()
        errorContainer.flipVisibility()
    }

    private fun movieOnClickListener(movieId: Int) {
        navigate(
            R.id.action_landingFragment_to_movieDetailFragment,
            MovieDetailArgs(movieId)
        )
    }

    private fun tvOnClickListener(tvId: Int) {
        navigate(
            R.id.action_landingFragment_to_tvDetailFragment,
            TvDetailArgs(tvId)
        )
    }

    override fun log(message: String) {
        Timber.d("sag debug: $message")
    }

    override fun log(throwable: Throwable) {
        Timber.e("sag error: $throwable")
    }
}
