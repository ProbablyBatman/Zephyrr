package greenberg.moviedbshell.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.adapters.CastListAdapter
import greenberg.moviedbshell.extensions.processAsReleaseDate
import greenberg.moviedbshell.extensions.processGenreTitle
import greenberg.moviedbshell.extensions.processGenres
import greenberg.moviedbshell.extensions.processLastOrNextAirDate
import greenberg.moviedbshell.extensions.processLastOrNextAirDateTitle
import greenberg.moviedbshell.extensions.processRatingInfo
import greenberg.moviedbshell.extensions.processRuntimes
import greenberg.moviedbshell.state.PersonDetailArgs
import greenberg.moviedbshell.state.TvDetailState
import greenberg.moviedbshell.viewmodel.TvDetailViewModel
import timber.log.Timber

class TvDetailFragment : BaseFragment() {

    val tvDetailViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.tvDetailViewModelFactory
    }

    private val viewModel: TvDetailViewModel by fragmentViewModel()

    private var progressBar: ProgressBar? = null
    private var scrollView: NestedScrollView? = null
    private var posterImageContainer: FrameLayout? = null
    private var posterImageView: ImageView? = null
    private var backgroundImageView: ImageView? = null
    private var backgroundImageViewContainer: FrameLayout? = null
    private var titleBar: TextView? = null

    private var firstAirDateText: TextView? = null
    private var firstAirDateTitle: TextView? = null
    private var lastOrNextAirDateText: TextView? = null
    private var lastOrNextAirDateTitle: TextView? = null
    private var statusText: TextView? = null
    private var statusTitle: TextView? = null
    private var numberOfEpisodesText: TextView? = null
    private var numberOfEpisodesTitle: TextView? = null
    private var numberOfSeasonsText: TextView? = null
    private var numberOfSeasonsTitle: TextView? = null
    private var userRatingsText: TextView? = null
    private var userRatingsTitle: TextView? = null
    private var runtimeText: TextView? = null
    private var runtimeTitle: TextView? = null
    private var genresText: TextView? = null
    private var genresTitle: TextView? = null
    private var overviewText: TextView? = null
    private var castRecyclerView: RecyclerView? = null
    private var errorTextView: TextView? = null
    private var errorRetryButton: MaterialButton? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var castListAdapter: CastListAdapter

    private var tvDetailId = -1
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            tvDetailId = arguments?.get("TvDetailID") as? Int ?: -1
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tv_detail_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.tv_detail_progress_bar)
        scrollView = view.findViewById(R.id.tv_detail_scroll)
        posterImageContainer = view.findViewById(R.id.tv_detail_poster_image_container)
        posterImageView = view.findViewById(R.id.tv_detail_poster_image)
        backgroundImageView = view.findViewById(R.id.tv_detail_background_image)
        backgroundImageViewContainer = view.findViewById(R.id.tv_detail_background_image_container)
        titleBar = view.findViewById(R.id.tv_detail_title)
        firstAirDateText = view.findViewById(R.id.first_air_date)
        firstAirDateTitle = view.findViewById(R.id.first_air_date_title)
        lastOrNextAirDateText = view.findViewById(R.id.last_or_next_air_date)
        lastOrNextAirDateTitle = view.findViewById(R.id.last_or_next_air_date_title)
        statusText = view.findViewById(R.id.tv_detail_status)
        statusTitle = view.findViewById(R.id.tv_detail_status_title)
        numberOfEpisodesText = view.findViewById(R.id.number_of_episodes)
        numberOfEpisodesTitle = view.findViewById(R.id.number_of_episodes_title)
        numberOfSeasonsText = view.findViewById(R.id.number_of_seasons)
        numberOfSeasonsTitle = view.findViewById(R.id.number_of_seasons_title)
        userRatingsText = view.findViewById(R.id.tv_detail_user_rating)
        userRatingsTitle = view.findViewById(R.id.tv_detail_user_rating_title)
        runtimeText = view.findViewById(R.id.tv_detail_runtime)
        runtimeTitle = view.findViewById(R.id.tv_detail_runtime_title)
        genresText = view.findViewById(R.id.tv_detail_genres)
        genresTitle = view.findViewById(R.id.tv_detail_genres_title)
        overviewText = view.findViewById(R.id.tv_detail_overview)
        errorTextView = view.findViewById(R.id.tv_detail_error)
        errorRetryButton = view.findViewById(R.id.tv_detail_retry_button)
        castRecyclerView = view.findViewById(R.id.tv_detail_cast_members_recycler)

        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        castRecyclerView?.layoutManager = linearLayoutManager
        castListAdapter = CastListAdapter(onClickListener = this::onClickListener)
        castRecyclerView?.adapter = castListAdapter
        navController = findNavController()

        viewModel.fetchTvDetail()
    }

    private fun showLoading() {
        Timber.d("Showing loading")
        hideAllViews()
        hideErrorState()
        showLoadingBar()
    }

    private fun showTvDetails(tvDetailState: TvDetailState) {
        Timber.d("Showing tv details")
        val tvDetailItem = tvDetailState.tvDetailItem

        if (tvDetailItem != null) {
            Timber.d("posterURL: ${tvDetailItem.posterImageUrl}")
            if (tvDetailItem.posterImageUrl.isNotEmpty() && posterImageView != null) {
                val validUrl = resources.getString(R.string.poster_url_substitution, tvDetailItem.posterImageUrl)
                Glide.with(this)
                        .load(validUrl)
                        .apply(
                                RequestOptions()
                                        .placeholder(ColorDrawable(Color.LTGRAY))
                                        .fallback(ColorDrawable(Color.LTGRAY))
                                        .centerCrop()
                        )
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(posterImageView!!)
            }

            Timber.d("backdropURL: ${tvDetailItem.backgroundImageUrl}")
            if (tvDetailItem.posterImageUrl.isNotEmpty() && backgroundImageView != null) {
                val validUrl = resources.getString(R.string.poster_url_substitution, tvDetailItem.backgroundImageUrl)
                Glide.with(this)
                        .load(validUrl)
                        .apply(
                                RequestOptions()
                                        .placeholder(ColorDrawable(Color.LTGRAY))
                                        .fallback(ColorDrawable(Color.LTGRAY))
                                        .centerCrop()
                        )
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(backgroundImageView!!)
            }

            titleBar?.text = tvDetailItem.title
            firstAirDateText?.text = tvDetailItem.firstAirDate.processAsReleaseDate()
            // Both of these have to not be null to show them
            val lastOrNextAirDateTitleText = requireContext().processLastOrNextAirDateTitle(tvDetailItem)
            val lastOrNextAirDate = tvDetailItem.processLastOrNextAirDate()
            if (lastOrNextAirDateTitleText != null && lastOrNextAirDate != null) {
                lastOrNextAirDateTitle?.text = lastOrNextAirDateTitleText
                lastOrNextAirDateText?.text = lastOrNextAirDate
                lastOrNextAirDateTitle?.visibility = View.VISIBLE
                lastOrNextAirDateText?.visibility = View.VISIBLE
            }
            statusText?.text = tvDetailItem.status
            numberOfEpisodesText?.text = tvDetailItem.numberOfEpisodes.toString()
            numberOfSeasonsText?.text = tvDetailItem.numberOfSeasons.toString()
            userRatingsText?.text = requireContext().processRatingInfo(tvDetailItem.voteAverage, tvDetailItem.voteCount)
            runtimeText?.text = requireContext().processRuntimes(tvDetailItem.runtime)
            genresTitle?.text = requireContext().processGenreTitle(tvDetailItem.genres.size)
            genresText?.text = requireContext().processGenres(tvDetailItem.genres)
            overviewText?.text = tvDetailItem.overview

            castListAdapter.castMemberList = tvDetailItem.castMembers
            castListAdapter.notifyDataSetChanged()
        }
    }

    private fun showError(throwable: Throwable) {
        Timber.d("Showing Error")
        Timber.e(throwable)
        hideLoadingBar()
        showErrorState()
        errorRetryButton?.setOnClickListener {
            viewModel.fetchTvDetail()
            hideErrorState()
        }
    }

    private fun hideAllViews() {
        progressBar?.visibility = View.GONE
        scrollView?.visibility = View.GONE
        posterImageContainer?.visibility = View.GONE
        posterImageView?.visibility = View.GONE
        backgroundImageView?.visibility = View.GONE
        backgroundImageViewContainer?.visibility = View.GONE
        firstAirDateText?.visibility = View.GONE
        firstAirDateTitle?.visibility = View.GONE
        lastOrNextAirDateText?.visibility = View.GONE
        lastOrNextAirDateTitle?.visibility = View.GONE
        statusText?.visibility = View.GONE
        statusTitle?.visibility = View.GONE
        numberOfEpisodesText?.visibility = View.GONE
        numberOfEpisodesTitle?.visibility = View.GONE
        numberOfSeasonsText?.visibility = View.GONE
        numberOfSeasonsTitle?.visibility = View.GONE
        userRatingsText?.visibility = View.GONE
        userRatingsTitle?.visibility = View.GONE
        runtimeText?.visibility = View.GONE
        runtimeTitle?.visibility = View.GONE
        genresText?.visibility = View.GONE
        genresTitle?.visibility = View.GONE
        overviewText?.visibility = View.GONE
        castRecyclerView?.visibility = View.GONE
    }

    private fun showAllViews() {
        posterImageView?.visibility = View.VISIBLE
        scrollView?.visibility = View.VISIBLE
        posterImageContainer?.visibility = View.VISIBLE
        backgroundImageView?.visibility = View.VISIBLE
        backgroundImageViewContainer?.visibility = View.VISIBLE
        firstAirDateText?.visibility = View.VISIBLE
        firstAirDateTitle?.visibility = View.VISIBLE
        statusText?.visibility = View.VISIBLE
        statusTitle?.visibility = View.VISIBLE
        numberOfEpisodesText?.visibility = View.VISIBLE
        numberOfEpisodesTitle?.visibility = View.VISIBLE
        numberOfSeasonsText?.visibility = View.VISIBLE
        numberOfSeasonsTitle?.visibility = View.VISIBLE
        userRatingsText?.visibility = View.VISIBLE
        userRatingsTitle?.visibility = View.VISIBLE
        runtimeText?.visibility = View.VISIBLE
        runtimeTitle?.visibility = View.VISIBLE
        genresText?.visibility = View.VISIBLE
        genresTitle?.visibility = View.VISIBLE
        overviewText?.visibility = View.VISIBLE
        castRecyclerView?.visibility = View.VISIBLE
    }

    private fun showLoadingBar() {
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        progressBar?.visibility = View.GONE
    }

    private fun hideErrorState() {
        errorTextView?.visibility = View.GONE
        errorRetryButton?.visibility = View.GONE
    }

    private fun showErrorState() {
        errorTextView?.visibility = View.VISIBLE
        errorRetryButton?.visibility = View.VISIBLE
        scrollView?.visibility = View.VISIBLE
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            when (state.tvDetailResponse) {
                Uninitialized -> Timber.d("uninitialized")
                is Loading -> {
                    showLoading()
                }
                is Success -> {
                    showTvDetails(state)
                    hideLoadingBar()
                    showAllViews()
                }
                is Fail -> {
                    showError(state.tvDetailResponse.error)
                }
            }
        }
    }

    private fun onClickListener(personId: Int) {
        navigate(
                R.id.action_tvDetailFragment_to_personDetailFragment,
                PersonDetailArgs(personId)
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }
}
