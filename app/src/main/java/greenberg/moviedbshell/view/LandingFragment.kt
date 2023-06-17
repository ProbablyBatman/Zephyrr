package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.LandingAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.MovieLandingState
import greenberg.moviedbshell.state.TvDetailArgs
import greenberg.moviedbshell.state.TvLandingState
import greenberg.moviedbshell.viewmodel.LandingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LandingFragment : BaseFragment() {

    @Inject
    lateinit var landingViewModelFactory: LandingViewModel.Factory

    private val viewModel: LandingViewModel by viewModels {
        LandingViewModel.provideFactory(landingViewModelFactory, Dispatchers.IO)
    }

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
    private lateinit var topRatedTvAdapter: LandingAdapter
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
    private lateinit var topRatedTvLoadingBar: ProgressBar
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
        // TODO: Add single row retries
//        errorContainer = view.findViewById(R.id.landing_row_error_container)
//        errorContainer.setOnClickListener {
////            viewModel.retryLandingPageLists()
//        }

        recentlyReleasedLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        popularMovieLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        soonTMLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        popularTvLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        topRatedTvLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recentlyReleasedAdapter = LandingAdapter(posterClickListener = this::movieOnClickListener)
        popularMovieAdapter = LandingAdapter(posterClickListener = this::movieOnClickListener)
        soonTMAdapter = LandingAdapter(posterClickListener = this::movieOnClickListener)
        popularTvAdapter = LandingAdapter(posterClickListener = this::tvOnClickListener)
        topRatedTvAdapter = LandingAdapter(posterClickListener = this::tvOnClickListener)

        recentlyReleasedRecycler.adapter = recentlyReleasedAdapter
        recentlyReleasedRecycler.layoutManager = recentlyReleasedLayoutManager

        popularMovieRecycler.adapter = popularMovieAdapter
        popularMovieRecycler.layoutManager = popularMovieLayoutManager

        soonTMRecycler.adapter = soonTMAdapter
        soonTMRecycler.layoutManager = soonTMLayoutManager

        popularTvRecycler.adapter = popularTvAdapter
        popularTvRecycler.layoutManager = popularTvLayoutManager

        topRatedTvRecycler.adapter = topRatedTvAdapter
        topRatedTvRecycler.layoutManager = topRatedTvLayoutManager

        contentContainer = view.findViewById(R.id.content_container)
        recentlyReleasedContainer = view.findViewById(R.id.recently_released_container)
        recentlyReleasedLoadingBar = view.findViewById(R.id.recently_released_progress_bar)
        recentlyReleasedSeeAllButton = view.findViewById(R.id.recently_released_see_all_button)
        recentlyReleasedSeeAllButton.setOnClickListener {
            navigate(R.id.action_landingFragment_to_recentlyReleasedFragment)
        }
        popularMovieContainer = view.findViewById(R.id.popular_movie_container)
        popularMovieLoadingBar = view.findViewById(R.id.popular_movie_progress_bar)
        popularMovieSeeAllButton = view.findViewById(R.id.popular_see_all_button)
        popularMovieSeeAllButton.setOnClickListener {
            navigate(R.id.action_landingFragment_to_popularMovieFragment)
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
        topRatedTvLoadingBar = view.findViewById(R.id.top_rated_tv_progress_bar)
        topRatedTvSeeAllButton = view.findViewById(R.id.top_rated_tv_see_all_button)
        topRatedTvSeeAllButton.setOnClickListener {
            // TODO
        }

        registerObservers()
    }

    private fun registerObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.recentlyReleasedLandingState.collect {
                        updateRecentlyReleasedRow(it)
                    }
                }

                launch {
                    viewModel.popularMoviesLandingState.collect {
                        updatePopularMoviesRow(it)
                    }
                }

                launch {
                    viewModel.soonTMMoviesLandingState.collect {
                        updateSoonTMRow(it)
                    }
                }

                launch {
                    viewModel.popularTvLandingState.collect {
                        updatePopularTvRow(it)
                    }
                }

                launch {
                    viewModel.topRatedTvLandingState.collect {
                        updateTopRatedTvRow(it)
                    }
                }
            }
        }
    }

    private fun updatePopularMoviesRow(state: MovieLandingState) {
        log("landing popular state is $state")
        when {
            state.isLoading -> {
                setContainerParamsLoading(popularMovieContainer)
                popularMovieLoadingBar.visibility = View.VISIBLE
                popularMovieRecycler.visibility = View.GONE
            }
            state.error != null -> {
                // TODO: display error
            }
            else -> {
                setContainerParamsNormal(popularMovieContainer)
                popularMovieAdapter.setItems(state.response)
                popularMovieLoadingBar.visibility = View.GONE
                popularMovieRecycler.visibility = View.VISIBLE
            }
        }
    }

    private fun updateRecentlyReleasedRow(state: MovieLandingState) {
        log("landing recently released state is $state")
        when {
            state.isLoading -> {
                setContainerParamsLoading(recentlyReleasedContainer)
                recentlyReleasedLoadingBar.visibility = View.VISIBLE
                recentlyReleasedRecycler.visibility = View.GONE
            }
            state.error != null -> {
                // TODO: display error
            }
            else -> {
                setContainerParamsNormal(recentlyReleasedContainer)
                recentlyReleasedAdapter.setItems(state.response)
                recentlyReleasedLoadingBar.visibility = View.GONE
                recentlyReleasedRecycler.visibility = View.VISIBLE
            }
        }
    }

    private fun updateSoonTMRow(state: MovieLandingState) {
        log("landing soon TM state is $state")
        when {
            state.isLoading -> {
                setContainerParamsLoading(soonTMContainer)
                soonTMLoadingBar.visibility = View.VISIBLE
                soonTMRecycler.visibility = View.GONE
            }
            state.error != null -> {
                // TODO: display error
            }
            else -> {
                setContainerParamsNormal(soonTMContainer)
                soonTMAdapter.setItems(state.response)
                soonTMLoadingBar.visibility = View.GONE
                soonTMRecycler.visibility = View.VISIBLE
            }
        }
    }

    private fun updatePopularTvRow(state: TvLandingState) {
        log("landing popular tv state is $state")
        when {
            state.isLoading -> {
                setContainerParamsLoading(popularTvContainer)
                popularTvLoadingBar.visibility = View.VISIBLE
                popularTvRecycler.visibility = View.GONE
            }
            state.error != null -> {
                // TODO: display error
            }
            else -> {
                setContainerParamsNormal(popularTvContainer)
                popularTvAdapter.setItems(state.response)
                popularTvLoadingBar.visibility = View.GONE
                popularTvRecycler.visibility = View.VISIBLE
            }
        }
    }

    private fun updateTopRatedTvRow(state: TvLandingState) {
        log("landing top rated tv state is $state")
        when {
            state.isLoading -> {
                setContainerParamsLoading(topRatedTvContainer)
                topRatedTvLoadingBar.visibility = View.VISIBLE
                topRatedTvRecycler.visibility = View.GONE
            }
            state.error != null -> {
                // TODO: display error
            }
            else -> {
                setContainerParamsNormal(topRatedTvContainer)
                topRatedTvAdapter.setItems(state.response)
                topRatedTvLoadingBar.visibility = View.GONE
                topRatedTvRecycler.visibility = View.VISIBLE
            }
        }
    }

    private fun setContainerParamsLoading(container: View) {
        val params = container.layoutParams
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = resources.getDimensionPixelSize(R.dimen.generic_poster_height)
        container.layoutParams = params
    }

    private fun setContainerParamsNormal(container: View) {
        val params = container.layoutParams
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        container.layoutParams = params
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
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}
