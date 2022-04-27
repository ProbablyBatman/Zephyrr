package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.LandingAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.state.LandingState
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.TvDetailArgs
import greenberg.moviedbshell.viewmodel.LandingViewModel
import timber.log.Timber

class LandingFragment : BaseFragment() {

    // TODO: INVESTIGATE REPLACING THIS WITH LATEINIT VAR INJECT
    val landingViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.landingViewModelFactory
    }

//    private val viewModel: LandingViewModel by fragmentViewModel()

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
    private lateinit var recentlyReleasedErrorContainer: ConstraintLayout
    private lateinit var recentlyReleasedLoadingBar: ProgressBar
    private lateinit var popularMovieSeeAllButton: Button
    private lateinit var popularMovieContainer: View
    private lateinit var popularMovieErrorContainer: ConstraintLayout
    private lateinit var popularMovieLoadingBar: ProgressBar
    private lateinit var soonTMSeeAllButton: Button
    private lateinit var soonTMContainer: View
    private lateinit var soonTMErrorContainer: ConstraintLayout
    private lateinit var soonTMLoadingBar: ProgressBar
    private lateinit var popularTvSeeAllButton: Button
    private lateinit var popularTvContainer: View
    private lateinit var popularTvLoadingBar: ProgressBar
    private lateinit var popularTvErrorContainer: ConstraintLayout
    private lateinit var topRatedTvSeeAllButton: Button
    private lateinit var topRatedTvContainer: View
    private lateinit var topRatedTvErrorContainer: ConstraintLayout
    private lateinit var topRatedLoadingBar: ProgressBar
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
        recentlyReleasedErrorContainer = view.findViewById(R.id.recently_released_error_container)
        recentlyReleasedErrorContainer.setOnClickListener {
//            viewModel.retryRecentlyReleased()
        }
        recentlyReleasedSeeAllButton = view.findViewById(R.id.recently_released_see_all_button)
        recentlyReleasedSeeAllButton.setOnClickListener {
            navigate(R.id.action_landingFragment_to_recentlyReleasedFragment)
        }
        popularMovieContainer = view.findViewById(R.id.popular_movie_container)
        popularMovieLoadingBar = view.findViewById(R.id.popular_movie_progress_bar)
        popularMovieErrorContainer = view.findViewById(R.id.popular_movie_error_container)
        popularMovieErrorContainer.setOnClickListener {
//            viewModel.retryPopularMovies()
        }
        popularMovieSeeAllButton = view.findViewById(R.id.popular_see_all_button)
        popularMovieSeeAllButton.setOnClickListener {
            navigate(R.id.action_landingFragment_to_popularMovieFragment)
        }
        soonTMContainer = view.findViewById(R.id.soon_tm_container)
        soonTMLoadingBar = view.findViewById(R.id.soon_tm_progress_bar)
        soonTMErrorContainer = view.findViewById(R.id.soon_tm_error_container)
        soonTMErrorContainer.setOnClickListener {
//            viewModel.retrySoonTM()
        }
        soonTMSeeAllButton = view.findViewById(R.id.soon_tm_see_all_button)
        soonTMSeeAllButton.setOnClickListener {
            navigate(R.id.action_landingFragment_to_soonTMFragment)
        }
        popularTvContainer = view.findViewById(R.id.popular_tv_container)
        popularTvLoadingBar = view.findViewById(R.id.popular_tv_progress_bar)
        popularTvErrorContainer = view.findViewById(R.id.popular_tv_error_container)
        popularTvErrorContainer.setOnClickListener {
//            viewModel.retryPopularTv()
        }
        popularTvSeeAllButton = view.findViewById(R.id.popular_tv_see_all_button)
        popularTvSeeAllButton.setOnClickListener {
            // TODO
        }
        topRatedTvContainer = view.findViewById(R.id.top_rated_tv_container)
        topRatedLoadingBar = view.findViewById(R.id.top_rated_tv_progress_bar)
        topRatedTvErrorContainer = view.findViewById(R.id.top_rated_tv_error_container)
        topRatedTvErrorContainer.setOnClickListener {
//            viewModel.retryTopRatedTv()
        }
        topRatedTvSeeAllButton = view.findViewById(R.id.top_rated_tv_see_all_button)
        topRatedTvSeeAllButton.setOnClickListener {
            // TODO
        }

//        viewModel.onEach { log("state is $it") }
    }

//    override fun invalidate() {
//        withState(viewModel) { state ->
//            updatePopularMoviesRow(state)
//            updateRecentlyReleasedRow(state)
//            updateSoonTMRow(state)
//            updatePopularTvRow(state)
//            updateTopRatedTvRow(state)
//        }
//    }

    private fun updateRecentlyReleasedRow(state: LandingState) {
        val response = state.recentlyReleasedResponse
        val movieList = state.recentlyReleasedItems
        when (response) {
            Uninitialized, is Loading -> {
                // load row
                log("recent release load")
                // TODO: bug here. Technically because I can't measure the title height in time to do this,
                // the entire row shifts a little bit after the images load in. For now, this will suffice.
                setContainerParamsLoading(recentlyReleasedContainer)
                recentlyReleasedErrorContainer.visibility = View.GONE
                recentlyReleasedLoadingBar.visibility = View.VISIBLE
                recentlyReleasedRecycler.visibility = View.GONE
            }
            is Success -> {
                // show row
                log("recent release success")
                setContainerParamsNormal(recentlyReleasedContainer)
                recentlyReleasedAdapter.setItems(movieList)
                recentlyReleasedLoadingBar.visibility = View.GONE
                recentlyReleasedRecycler.visibility = View.VISIBLE
            }
            is Fail -> {
                // error for row
//                setContainerParamsNormal(recentlyReleasedContainer)
                recentlyReleasedErrorContainer.visibility = View.VISIBLE
                recentlyReleasedLoadingBar.visibility = View.GONE
                recentlyReleasedRecycler.visibility = View.GONE
            }
        }
    }

    private fun updatePopularMoviesRow(state: LandingState) {
        val response = state.popularMovieResponse
        val movieList = state.popularMovieItems
        when (response) {
            // TODO I would use Incomplete here as Mavericks suggests, but kotlin gives me a warning
            Uninitialized, is Loading -> {
                // load row
                log("popular movie load")
                setContainerParamsLoading(popularMovieContainer)
                popularMovieErrorContainer.visibility = View.GONE
                popularMovieLoadingBar.visibility = View.VISIBLE
                popularMovieRecycler.visibility = View.GONE
            }
            is Success -> {
                log("popular movie success")
                // show row
                setContainerParamsNormal(popularMovieContainer)
                popularMovieAdapter.setItems(movieList)
                popularMovieLoadingBar.visibility = View.GONE
                popularMovieRecycler.visibility = View.VISIBLE
            }
            is Fail -> {
                // error for row
                setContainerParamsNormal(popularMovieContainer)
                popularMovieErrorContainer.visibility = View.VISIBLE
                popularMovieLoadingBar.visibility = View.GONE
                popularMovieRecycler.visibility = View.GONE
            }
        }
    }

    private fun updateSoonTMRow(state: LandingState) {
        val response = state.soonTMResponse
        val movieList = state.soonTMItems
        when (response) {
            Uninitialized, is Loading -> {
                // load row
                log("soontm load")
                setContainerParamsLoading(soonTMContainer)
                soonTMErrorContainer.visibility = View.GONE
                soonTMLoadingBar.visibility = View.VISIBLE
                soonTMRecycler.visibility = View.GONE
            }
            is Success -> {
                // show row
                log("soontm success")
                setContainerParamsNormal(soonTMContainer)
                soonTMAdapter.setItems(movieList)
                soonTMLoadingBar.visibility = View.GONE
                soonTMRecycler.visibility = View.VISIBLE
            }
            is Fail -> {
                // error for row
                setContainerParamsNormal(soonTMContainer)
                soonTMErrorContainer.visibility = View.VISIBLE
                soonTMLoadingBar.visibility = View.GONE
                soonTMRecycler.visibility = View.GONE
            }
        }
    }

    private fun updatePopularTvRow(state: LandingState) {
        val response = state.popularTvResponse
        val tvList = state.popularTvItems
        when (response) {
            Uninitialized, is Loading -> {
                // load row
                log("popular tv load")
                setContainerParamsLoading(popularTvContainer)
                popularTvErrorContainer.visibility = View.GONE
                popularTvLoadingBar.visibility = View.VISIBLE
                popularTvRecycler.visibility = View.GONE
            }
            is Success -> {
                // show row
                log("popular tv success")
                setContainerParamsNormal(popularTvContainer)
                popularTvAdapter.setItems(tvList)
                popularTvLoadingBar.visibility = View.GONE
                popularTvRecycler.visibility = View.VISIBLE
            }
            is Fail -> {
                // error for row
                setContainerParamsNormal(popularTvContainer)
                popularTvErrorContainer.visibility = View.VISIBLE
                popularTvLoadingBar.visibility = View.GONE
                popularTvRecycler.visibility = View.GONE
            }
        }
    }

    private fun updateTopRatedTvRow(state: LandingState) {
        val response = state.topRatedTvResponse
        val tvList = state.topRatedTvItems
        when (response) {
            Uninitialized, is Loading -> {
                // load row
                log("top rated load")
                setContainerParamsLoading(topRatedTvContainer)
                topRatedTvErrorContainer.visibility = View.GONE
                topRatedLoadingBar.visibility = View.VISIBLE
                topRatedTvRecycler.visibility = View.GONE
            }
            is Success -> {
                // show row
                log("top rated success")
                setContainerParamsNormal(topRatedTvContainer)
                topRatedAdapter.setItems(tvList)
                topRatedLoadingBar.visibility = View.GONE
                topRatedTvRecycler.visibility = View.VISIBLE
            }
            is Fail -> {
                // error for row
                setContainerParamsNormal(topRatedTvContainer)
                topRatedTvErrorContainer.visibility = View.VISIBLE
                topRatedLoadingBar.visibility = View.GONE
                topRatedTvRecycler.visibility = View.GONE
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
