package greenberg.moviedbshell.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.adapters.CastListAdapter
import greenberg.moviedbshell.adapters.PosterListAdapter
import greenberg.moviedbshell.extensions.processDate
import greenberg.moviedbshell.extensions.processNetworks
import greenberg.moviedbshell.extensions.processNetworksTitle
import greenberg.moviedbshell.extensions.processRatingInfo
import greenberg.moviedbshell.extensions.processRuntimeTitle
import greenberg.moviedbshell.extensions.processRuntimes
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.ui.AggregateCastMemberItem
import greenberg.moviedbshell.models.ui.ProductionCompanyItem
import greenberg.moviedbshell.models.ui.ProductionCountryItem
import greenberg.moviedbshell.models.ui.TvDetailItem
import greenberg.moviedbshell.state.CastStateArgs
import greenberg.moviedbshell.state.PersonDetailArgs
import greenberg.moviedbshell.state.PosterImageGalleryArgs
import greenberg.moviedbshell.state.ProductionDetailStateArgs
import greenberg.moviedbshell.state.TvDetailState
import greenberg.moviedbshell.viewmodel.TvDetailViewModel
import timber.log.Timber

class TvDetailFragment : BaseFragment() {

    val tvDetailViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.tvDetailViewModelFactory
    }

    private val viewModel: TvDetailViewModel by fragmentViewModel()

    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: NestedScrollView
    private lateinit var posterImageContainer: ConstraintLayout
    private lateinit var posterRecycler: RecyclerView
    private lateinit var posterSeeAllButton: Button
    private lateinit var posterListAdapter: PosterListAdapter
    private lateinit var backgroundImageView: ImageView
    private lateinit var backdropImageContainer: FrameLayout
    private lateinit var titleBar: TextView
    private lateinit var firstAirDateText: TextView
    private lateinit var firstAirDateTitle: TextView
    private lateinit var lastAirDateText: TextView
    private lateinit var lastAirDateTitle: TextView
    private lateinit var nextAirDateText: TextView
    private lateinit var nextAirDateTitle: TextView
    private lateinit var createdByText: TextView
    private lateinit var createdByTitle: TextView
    private lateinit var statusText: TextView
    private lateinit var statusTitle: TextView
    private lateinit var numberOfEpisodesText: TextView
    private lateinit var numberOfEpisodesTitle: TextView
    private lateinit var numberOfSeasonsText: TextView
    private lateinit var numberOfSeasonsTitle: TextView
    private lateinit var userRatingsText: TextView
    private lateinit var userRatingsTitle: TextView
    private lateinit var runtimeText: TextView
    private lateinit var runtimeTitle: TextView
    private lateinit var genreChipGroup: ChipGroup
    private lateinit var overviewText: TextView
    private lateinit var overviewTitle: TextView
    private lateinit var networksText: TextView
    private lateinit var networksTitle: TextView
    private lateinit var productionCompaniesText: TextView
    private lateinit var productionCompaniesTitle: TextView
    private lateinit var filmingLocationsText: TextView
    private lateinit var filmingLocationsTitle: TextView
    private lateinit var castContainer: ConstraintLayout
    private lateinit var castRecyclerView: RecyclerView
    private lateinit var castSeeAllButton: Button
    private lateinit var productionSeeAllButton: Button
    private lateinit var errorTextView: TextView
    private lateinit var errorRetryButton: Button
    private lateinit var castListAdapter: CastListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tv_detail_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.tv_detail_progress_bar)
        scrollView = view.findViewById(R.id.tv_detail_scroll)
        posterImageContainer = view.findViewById(R.id.poster_gallery_container)
        posterRecycler = view.findViewById(R.id.poster_recycler)
        posterSeeAllButton = view.findViewById(R.id.poster_see_all_button)
        backgroundImageView = view.findViewById(R.id.tv_detail_background_image)
        backdropImageContainer = view.findViewById(R.id.tv_detail_background_image_container)
        titleBar = view.findViewById(R.id.tv_detail_title)
        firstAirDateText = view.findViewById(R.id.detail_first_episode_air_date)
        firstAirDateTitle = view.findViewById(R.id.detail_first_episode_air_date_title)
        lastAirDateText = view.findViewById(R.id.detail_last_episode_date)
        lastAirDateTitle = view.findViewById(R.id.detail_last_episode_date_title)
        nextAirDateText = view.findViewById(R.id.detail_next_episode_air_date)
        nextAirDateTitle = view.findViewById(R.id.detail_next_episode_air_date_title)
        statusText = view.findViewById(R.id.detail_status)
        statusTitle = view.findViewById(R.id.detail_status_title)
        numberOfEpisodesText = view.findViewById(R.id.detail_episode_count)
        numberOfEpisodesTitle = view.findViewById(R.id.detail_episode_count_title)
        numberOfSeasonsText = view.findViewById(R.id.detail_seasons)
        numberOfSeasonsTitle = view.findViewById(R.id.detail_seasons_title)
        userRatingsText = view.findViewById(R.id.detail_user_rating)
        userRatingsTitle = view.findViewById(R.id.detail_user_rating_title)
        runtimeText = view.findViewById(R.id.detail_runtime)
        runtimeTitle = view.findViewById(R.id.detail_runtime_title)
        genreChipGroup = view.findViewById(R.id.tv_genre_chips)
        overviewText = view.findViewById(R.id.tv_detail_overview)
        overviewTitle = view.findViewById(R.id.overview_row_title)
        filmingLocationsText = view.findViewById(R.id.detail_filming_locations_preview)
        filmingLocationsTitle = view.findViewById(R.id.detail_filming_locations_title)
        productionCompaniesText = view.findViewById(R.id.detail_production_companies_preview)
        productionCompaniesTitle = view.findViewById(R.id.detail_production_companies_title)
        createdByText = view.findViewById(R.id.detail_created_by)
        createdByTitle = view.findViewById(R.id.detail_created_by_title)
        networksText = view.findViewById(R.id.detail_networks)
        networksTitle = view.findViewById(R.id.detail_networks_title)
        errorTextView = view.findViewById(R.id.tv_detail_error)
        errorRetryButton = view.findViewById(R.id.tv_detail_retry_button)
        castContainer = view.findViewById(R.id.cast_recycler_container)
        castRecyclerView = view.findViewById(R.id.cast_members_recycler)
        castSeeAllButton = view.findViewById(R.id.cast_see_all_button)
        productionSeeAllButton = view.findViewById(R.id.production_see_all_button)

        castListAdapter = CastListAdapter(onClickListener = this::onClickListener, isBubble = true)
        castRecyclerView.apply {
            adapter = castListAdapter
            layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
        }

        posterListAdapter = PosterListAdapter()
        posterRecycler.apply {
            adapter = posterListAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun showLoading() {
        Timber.d("Showing loading")
        hideAllViews()
        hideErrorState()
        showLoadingBar()
    }

    private fun showTvDetails(state: TvDetailState) {
        Timber.d("Showing tv details")
        val tvDetailItem = state.tvDetailItem

        if (tvDetailItem != null) {
            Timber.d("backdropURL: ${tvDetailItem.backgroundImageUrl}")
            posterListAdapter.posterItems = tvDetailItem.posterUrls.take(POSTER_PREVIEW_VALUE)
            posterListAdapter.notifyDataSetChanged()

            if (tvDetailItem.posterImageUrl.isNotEmpty()) {
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
                    .into(backgroundImageView)
            }

            titleBar.text = tvDetailItem.title
            overviewText.text = tvDetailItem.overview

            castListAdapter.setCastMembers(tvDetailItem.castMembers.take(CAST_PREVIEW_VALUE))
            castListAdapter.notifyDataSetChanged()
            // TODO: one day, clicking a genre will lead to a genre filtered screen
            if (genreChipGroup.childCount == 0) {
                tvDetailItem.genres.forEach {
                    val chip = Chip(requireContext())
                        .apply {
                            text = it
                        }
                    genreChipGroup.addView(chip)
                }
            }
            firstAirDateText.text = tvDetailItem.firstAirDate.processDate()
            lastAirDateText.text = tvDetailItem.lastAirDate.processDate()
            if (tvDetailItem.nextAirDate.isNotBlank()) {
                nextAirDateText.text = tvDetailItem.nextAirDate.processDate()
            } else {
                nextAirDateTitle.visibility = View.GONE
                nextAirDateText.visibility = View.GONE
            }
            statusText.text = tvDetailItem.status
            numberOfEpisodesText.text = tvDetailItem.numberOfEpisodes.toString()
            numberOfSeasonsText.text = tvDetailItem.numberOfSeasons.toString()
            userRatingsText.text = requireContext().processRatingInfo(tvDetailItem.voteAverage, tvDetailItem.voteCount)
            runtimeTitle.text = requireContext().processRuntimeTitle(tvDetailItem.runtime)
            runtimeText.text = requireContext().processRuntimes(tvDetailItem.runtime)
            networksTitle.text = requireContext().processNetworksTitle(tvDetailItem.networks)
            networksText.text = processNetworks(tvDetailItem.networks)
            overviewText.text = tvDetailItem.overview
            productionCompaniesText.text = tvDetailItem.productionCompanies.getOrElse(0) { ProductionCompanyItem.generateDummy() }.name
            filmingLocationsText.text = tvDetailItem.productionCountries.getOrElse(0) { ProductionCountryItem.generateDummy() }.name
            createdByText.text = tvDetailItem.createdBy.joinToString(", ") { it.name }
            backdropImageContainer.setOnClickListener {
                ImageGalleryDialog()
                    .apply {
                        arguments = Bundle().apply {
                            putParcelable(MvRx.KEY_ARG, PosterImageGalleryArgs(state.tvId, MediaType.TV))
                            putBoolean(ImageGalleryDialog.BACKDROP_KEY, true)
                        }
                    }
                    .show(parentFragmentManager, ImageGalleryDialog.TAG)
            }
            posterSeeAllButton.setOnClickListener {
                ImageGalleryDialog()
                    .apply {
                        arguments = Bundle().apply {
                            putParcelable(MvRx.KEY_ARG, PosterImageGalleryArgs(state.tvId, MediaType.TV))
                            putBoolean(ImageGalleryDialog.BACKDROP_KEY, false)
                        }
                    }
                    .show(parentFragmentManager, ImageGalleryDialog.TAG)
            }
            castSeeAllButton.setOnClickListener { castSeeAllOnClickListener(tvDetailItem.aggregateCastMembers) }
            productionSeeAllButton.setOnClickListener { productionSeeAllOnClickListener(tvDetailItem) }
        }
    }

    private fun showError(throwable: Throwable) {
        Timber.d("Showing Error")
        Timber.e(throwable)
        hideLoadingBar()
        showErrorState()
        errorRetryButton.setOnClickListener {
            viewModel.fetchTvDetail()
            hideErrorState()
        }
    }

    private fun hideAllViews() {
        scrollView.visibility = View.GONE
        posterImageContainer.visibility = View.GONE
        backgroundImageView.visibility = View.GONE
        backdropImageContainer.visibility = View.GONE
        firstAirDateText.visibility = View.GONE
        firstAirDateTitle.visibility = View.GONE
        statusText.visibility = View.GONE
        statusTitle.visibility = View.GONE
        numberOfEpisodesText.visibility = View.GONE
        numberOfEpisodesTitle.visibility = View.GONE
        numberOfSeasonsText.visibility = View.GONE
        numberOfSeasonsTitle.visibility = View.GONE
        userRatingsText.visibility = View.GONE
        userRatingsTitle.visibility = View.GONE
        runtimeText.visibility = View.GONE
        runtimeTitle.visibility = View.GONE
        overviewText.visibility = View.GONE
        castRecyclerView.visibility = View.GONE
    }

    private fun showAllViews() {
        scrollView.visibility = View.VISIBLE
        posterImageContainer.visibility = View.VISIBLE
        backgroundImageView.visibility = View.VISIBLE
        backdropImageContainer.visibility = View.VISIBLE
        firstAirDateText.visibility = View.VISIBLE
        firstAirDateTitle.visibility = View.VISIBLE
        statusText.visibility = View.VISIBLE
        statusTitle.visibility = View.VISIBLE
        numberOfEpisodesText.visibility = View.VISIBLE
        numberOfEpisodesTitle.visibility = View.VISIBLE
        numberOfSeasonsText.visibility = View.VISIBLE
        numberOfSeasonsTitle.visibility = View.VISIBLE
        userRatingsText.visibility = View.VISIBLE
        userRatingsTitle.visibility = View.VISIBLE
        runtimeText.visibility = View.VISIBLE
        runtimeTitle.visibility = View.VISIBLE
        overviewText.visibility = View.VISIBLE
        castRecyclerView.visibility = View.VISIBLE
    }

    private fun showLoadingBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        progressBar.visibility = View.GONE
    }

    private fun hideErrorState() {
        errorTextView.visibility = View.GONE
        errorRetryButton.visibility = View.GONE
    }

    private fun showErrorState() {
        errorTextView.visibility = View.VISIBLE
        errorRetryButton.visibility = View.VISIBLE
        scrollView.visibility = View.VISIBLE
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

    private fun castSeeAllOnClickListener(castList: List<AggregateCastMemberItem>) {
        navigate(
            R.id.action_tvDetailFragment_to_castFragment,
            CastStateArgs(castList)
        )
    }

    private fun productionSeeAllOnClickListener(tvDetailItem: TvDetailItem) {
        navigate(
            R.id.action_tvDetailFragment_to_productionDetailFragment,
            ProductionDetailStateArgs(tvDetailItem)
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }

    companion object {
        @JvmField
        val TAG: String = MovieDetailFragment::class.java.simpleName

        private const val CAST_PREVIEW_VALUE = 6
        private const val POSTER_PREVIEW_VALUE = 8
    }
}
