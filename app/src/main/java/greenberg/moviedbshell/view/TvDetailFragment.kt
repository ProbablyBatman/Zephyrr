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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.models.ui.TvDetailItem
import greenberg.moviedbshell.presenters.TvDetailPresenter
import greenberg.moviedbshell.viewHolders.CastListAdapter
import timber.log.Timber

class TvDetailFragment :
        BaseFragment<TvDetailView, TvDetailPresenter>(),
        TvDetailView {

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
        castListAdapter = CastListAdapter()
        castRecyclerView?.adapter = castListAdapter

        presenter.initView(tvDetailId, castListAdapter)
        presenter.loadTvDetails(tvDetailId)
        navController = findNavController()
    }

    override fun createPresenter() = presenter
            ?: (activity?.application as ZephyrrApplication).component.tvDetailPresenter()

    override fun showLoading(tvShowId: Int) {
        Timber.d("Showing loading")
        hideAllViews()
        hideErrorState()
        showLoadingBar()
    }

    override fun showTvDetails(tvDetailItem: TvDetailItem) {
        Timber.d("Showing tv details")

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
        firstAirDateText?.text = presenter.processDate(tvDetailItem.firstAirDate)
        // Both of these have to not be null to show them
        val lastOrNextAirDateTitleText = presenter.processLastOrNextAirDateTitle(tvDetailItem)
        val lastOrNextAirDate = presenter.processLastOrNextAirDate(tvDetailItem)
        if (lastOrNextAirDateTitleText != null && lastOrNextAirDate != null) {
            lastOrNextAirDateTitle?.text = lastOrNextAirDateTitleText
            lastOrNextAirDateText?.text = lastOrNextAirDate
            lastOrNextAirDateTitle?.visibility = View.VISIBLE
            lastOrNextAirDateText?.visibility = View.VISIBLE
        }
        statusText?.text = tvDetailItem.status
        numberOfEpisodesText?.text = tvDetailItem.numberOfEpisodes.toString()
        numberOfSeasonsText?.text = tvDetailItem.numberOfSeasons.toString()
        userRatingsText?.text = presenter.processRatingInfo(tvDetailItem.voteAverage, tvDetailItem.voteCount)
        runtimeText?.text = presenter.processRuntime(tvDetailItem.runtime)
        genresTitle?.text = presenter.processGenreTitle(tvDetailItem.genres.size)
        genresText?.text = presenter.processGenres(tvDetailItem.genres)
        overviewText?.text = tvDetailItem.overview

        hideLoadingBar()
        showAllViews()
    }

    override fun showError(throwable: Throwable) {
        Timber.d("Showing Error")
        Timber.e(throwable)
        hideLoadingBar()
        showErrorState()
        errorRetryButton?.setOnClickListener {
            presenter.loadTvDetails(tvDetailId)
            hideErrorState()
        }
    }

    override fun showDetail(bundle: Bundle) {
        navController?.navigate(R.id.action_tvDetailFragment_to_personDetailFragment, bundle)
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

    override fun log(message: String) {
        Timber.d(message)
    }
}