package greenberg.moviedbshell.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.CastListAdapter
import greenberg.moviedbshell.adapters.PosterListAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.extensions.processAsReleaseDate
import greenberg.moviedbshell.extensions.processRatingInfo
import greenberg.moviedbshell.extensions.processRuntime
import greenberg.moviedbshell.models.ui.CastMemberItem
import greenberg.moviedbshell.models.ui.MovieDetailItem
import greenberg.moviedbshell.models.ui.ProductionCompanyItem
import greenberg.moviedbshell.models.ui.ProductionCountryItem
import greenberg.moviedbshell.state.CastStateArgs
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.MovieDetailState
import greenberg.moviedbshell.state.PersonDetailArgs
import greenberg.moviedbshell.view.ImageGalleryDialog.Companion.BACKDROP_KEY
import greenberg.moviedbshell.viewmodel.MovieDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MovieDetailFragment : BaseFragment() {

    // TODO: gracefully handle crash that this will cause if ID isn't present

    @Inject
    lateinit var movieDetailViewModelFactory: MovieDetailViewModel.Factory

    private val viewModel: MovieDetailViewModel by viewModels {
        MovieDetailViewModel.provideFactory(
            movieDetailViewModelFactory,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable(PAGE_ARGS, MovieDetailArgs::class.java)?.movieId
            } else {
                (arguments?.getParcelable(PAGE_ARGS) as? MovieDetailArgs)?.movieId
            } ?: -1,
            Dispatchers.IO
        )
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var posterGalleryContainer: ConstraintLayout
    private lateinit var backdropImageView: ImageView
    private lateinit var backdropImageContainer: FrameLayout
    private lateinit var titleBar: TextView
    private lateinit var scrollView: NestedScrollView
    private lateinit var castContainer: ConstraintLayout
    private lateinit var castRecyclerView: RecyclerView
    private lateinit var castSeeAllButton: Button
    private lateinit var productionRowTitle: TextView
    private lateinit var productionSeeAllButton: Button
    private lateinit var directorTitle: TextView
    private lateinit var directorTextView: TextView
    private lateinit var productionCompaniesTitle: TextView
    private lateinit var productionCompaniesTextView: TextView
    private lateinit var filmingLocationsTitle: TextView
    private lateinit var filmingLocationsTextView: TextView
    private lateinit var runtimeTextView: TextView
    private lateinit var runtimeTitle: TextView
    private lateinit var statusTextView: TextView
    private lateinit var statusTitle: TextView
    private lateinit var budgetTextView: TextView
    private lateinit var budgetTitle: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var releaseDateTitle: TextView
    private lateinit var ratingTextView: TextView
    private lateinit var ratingTitle: TextView
    private lateinit var performanceTitle: TextView
    private lateinit var revenueTitle: TextView
    private lateinit var revenueTextView: TextView
    private lateinit var overviewTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var errorRetryButton: Button
    private lateinit var errorGuideline: Guideline
    private lateinit var castListAdapter: CastListAdapter
    private lateinit var genreChipGroup: ChipGroup
    private lateinit var posterRecycler: RecyclerView
    private lateinit var posterSeeAllButton: Button
    private lateinit var posterListAdapter: PosterListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_detail_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: just writing this down here for later.
        // more than half of these could disappear in the current state, but the idea is that if
        // some of these movies lack any of this data, I want to selectively remove views from visibility
        progressBar = view.findViewById(R.id.movie_detail_progress_bar)
        backdropImageContainer = view.findViewById(R.id.movie_detail_background_image_container)
        posterGalleryContainer = view.findViewById(R.id.poster_gallery_container)
        titleBar = view.findViewById(R.id.movie_detail_title)
        backdropImageView = view.findViewById(R.id.movie_detail_background_image)
        scrollView = view.findViewById(R.id.movie_detail_scroll)
        castContainer = view.findViewById(R.id.cast_recycler_container)
        castRecyclerView = view.findViewById(R.id.cast_members_recycler)
        castSeeAllButton = view.findViewById(R.id.cast_see_all_button)
        productionRowTitle = view.findViewById(R.id.production_row_title)
        productionSeeAllButton = view.findViewById(R.id.production_see_all_button)
        directorTitle = view.findViewById(R.id.detail_director_title)
        directorTextView = view.findViewById(R.id.detail_director)
        productionCompaniesTitle = view.findViewById(R.id.detail_production_companies_title)
        productionCompaniesTextView = view.findViewById(R.id.detail_production_companies_preview)
        filmingLocationsTitle = view.findViewById(R.id.detail_filming_locations_title)
        filmingLocationsTextView = view.findViewById(R.id.detail_filming_locations_preview)
        overviewTextView = view.findViewById(R.id.movie_detail_overview)
        releaseDateTextView = view.findViewById(R.id.detail_release_date)
        releaseDateTitle = view.findViewById(R.id.detail_release_date_title)
        ratingTextView = view.findViewById(R.id.detail_user_rating)
        ratingTitle = view.findViewById(R.id.detail_user_rating_title)
        statusTextView = view.findViewById(R.id.detail_status)
        statusTitle = view.findViewById(R.id.detail_status_title)
        budgetTextView = view.findViewById(R.id.detail_budget)
        budgetTitle = view.findViewById(R.id.detail_budget_title)
        runtimeTextView = view.findViewById(R.id.detail_runtime)
        runtimeTitle = view.findViewById(R.id.detail_runtime_title)
        errorTextView = view.findViewById(R.id.movie_detail_error)
        errorRetryButton = view.findViewById(R.id.movie_detail_retry_button)
        errorGuideline = view.findViewById(R.id.movie_detail_error_guideline)
        performanceTitle = view.findViewById(R.id.performance_row_title)
        revenueTitle = view.findViewById(R.id.detail_revenue_title)
        revenueTextView = view.findViewById(R.id.detail_revenue)
        genreChipGroup = view.findViewById(R.id.movie_genre_chips)
        posterRecycler = view.findViewById(R.id.poster_recycler)
        posterSeeAllButton = view.findViewById(R.id.poster_see_all_button)

        castListAdapter = CastListAdapter(onClickListener = this::highlightedCastOnClickListener, isBubble = true)
        castRecyclerView.apply {
            adapter = castListAdapter
            layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
        }
        posterListAdapter = PosterListAdapter()
        posterRecycler.apply {
            adapter = posterListAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        registerObservers()
    }

    private fun registerObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.movieDetailState.collect {
                        updateMovieDetails(it)
                    }
                }
            }
        }
    }

    private fun updateMovieDetails(state: MovieDetailState) {
        when {
            state.isLoading -> {
                showLoading()
            }
            state.error != null -> {
                showError(state.error)
            }
            // TODO: handle if the item is empty?
            // Also this order is dumb, fix this
            state.movieDetailItem != null -> {
                showMovieDetails(state.movieDetailItem)
                hideLoadingBar()
                showAllViews()
                showContent()
            }
        }
    }

    private fun showLoading() {
        Timber.d("Show Loading")
        hideAllViews()
        hideErrorState()
        showLoadingBar()
    }

    private fun showError(throwable: Throwable) {
        Timber.d("Showing Error")
        Timber.e(throwable)
        hideLoadingBar()
        hideContent()
        showAllViews()
        showErrorState()
        errorRetryButton.setOnClickListener {
            viewModel.fetchMovieDetail()
            hideErrorState()
        }
    }

    private fun showMovieDetails(movieDetailItem: MovieDetailItem?) {
        log("Showing Movie Details")
        log("MovieDetails: $movieDetailItem")
        if (movieDetailItem != null) {
            posterListAdapter.posterItems = movieDetailItem.posterUrls.take(POSTER_PREVIEW_VALUE)
            posterListAdapter.notifyItemRangeChanged(0, posterListAdapter.itemCount)

            log("backdropURL: ${movieDetailItem.backdropImageUrl}")
            if (movieDetailItem.backdropImageUrl.isNotEmpty()) {
                val validUrl = resources.getString(R.string.poster_url_substitution, movieDetailItem.backdropImageUrl)
                Glide.with(this)
                    .load(validUrl)
                    .apply(
                        RequestOptions()
                            .placeholder(ColorDrawable(Color.LTGRAY))
                            .fallback(ColorDrawable(Color.LTGRAY))
                            .centerCrop()
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(backdropImageView)
            }

            castListAdapter.setCastMembers(movieDetailItem.castMembers.take(CAST_PREVIEW_VALUE))
            castListAdapter.notifyItemRangeChanged(0, castListAdapter.itemCount)
            // TODO: one day, clicking a genre will lead to a genre filtered screen
            if (genreChipGroup.childCount == 0) {
                movieDetailItem.genres.forEach {
                    val chip = Chip(requireContext())
                        .apply {
                            text = it
                        }
                    genreChipGroup.addView(chip)
                }
            }

            titleBar.text = movieDetailItem.title
            // TODO: investigate just hiding this view if it's null
            directorTextView.text = movieDetailItem.crewMembers.find { it.job.lowercase() == "director" }?.name
            productionCompaniesTextView.text = movieDetailItem.productionCompanies.getOrElse(0) { ProductionCompanyItem.generateDummy() }.name
            filmingLocationsTextView.text = movieDetailItem.productionCountries.getOrElse(0) { ProductionCountryItem.generateDummy() }.name
            overviewTextView.text = movieDetailItem.overview
            releaseDateTextView.text = movieDetailItem.releaseDate.processAsReleaseDate()
            ratingTextView.text = requireContext().processRatingInfo(movieDetailItem.voteAverage, movieDetailItem.voteCount)
            statusTextView.text = movieDetailItem.status
            runtimeTextView.text = requireContext().processRuntime(movieDetailItem.runtime)
            budgetTextView.text = resources.getString(R.string.budget_substitution, NumberFormat.getInstance(Locale.US).format(movieDetailItem.budget))
            revenueTextView.text = resources.getString(R.string.budget_substitution, NumberFormat.getInstance(Locale.US).format(movieDetailItem.revenue))
            backdropImageContainer.setOnClickListener {
                ImageGalleryDialog()
                    .apply {
                        arguments = Bundle().apply {
//                            putParcelable(Mavericks.KEY_ARG, PosterImageGalleryArgs(state.movieId, MediaType.MOVIE))
                            putBoolean(BACKDROP_KEY, true)
                        }
                    }
                    .show(parentFragmentManager, ImageGalleryDialog.TAG)
            }
            posterSeeAllButton.setOnClickListener {
                ImageGalleryDialog()
                    .apply {
                        arguments = Bundle().apply {
//                            putParcelable(Mavericks.KEY_ARG, PosterImageGalleryArgs(state.movieId, MediaType.MOVIE))
                            putBoolean(BACKDROP_KEY, false)
                        }
                    }
                    .show(parentFragmentManager, ImageGalleryDialog.TAG)
            }
            castSeeAllButton.setOnClickListener { castSeeAllOnClickListener(movieDetailItem.castMembers) }
            productionSeeAllButton.setOnClickListener { productionSeeAllOnClickListener() }
            // TODO: potentially scrape other rating information
        }
    }

    private fun hideAllViews() {
        progressBar.visibility = View.GONE
        scrollView.visibility = View.GONE
    }

    private fun showAllViews() {
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.VISIBLE
    }

    private fun showLoadingBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        progressBar.visibility = View.GONE
    }

    private fun hideErrorState() {
        errorGuideline.visibility = View.GONE
        errorTextView.visibility = View.GONE
        errorRetryButton.visibility = View.GONE
    }

    private fun showErrorState() {
        errorGuideline.visibility = View.VISIBLE
        errorTextView.visibility = View.VISIBLE
        errorRetryButton.visibility = View.VISIBLE
    }

    private fun hideContent() {
        backdropImageContainer.visibility = View.GONE
        genreChipGroup.visibility = View.GONE
        overviewTextView.visibility = View.GONE
        castContainer.visibility = View.GONE
        productionRowTitle.visibility = View.GONE
        productionSeeAllButton.visibility = View.GONE
        directorTitle.visibility = View.GONE
        directorTextView.visibility = View.GONE
        productionCompaniesTitle.visibility = View.GONE
        productionCompaniesTextView.visibility = View.GONE
        filmingLocationsTitle.visibility = View.GONE
        filmingLocationsTextView.visibility = View.GONE
        releaseDateTitle.visibility = View.GONE
        releaseDateTextView.visibility = View.GONE
        runtimeTitle.visibility = View.GONE
        runtimeTextView.visibility = View.GONE
        statusTitle.visibility = View.GONE
        statusTextView.visibility = View.GONE
        budgetTitle.visibility = View.GONE
        budgetTextView.visibility = View.GONE
        performanceTitle.visibility = View.GONE
        revenueTitle.visibility = View.GONE
        revenueTextView.visibility = View.GONE
        ratingTitle.visibility = View.GONE
        ratingTextView.visibility = View.GONE
        posterGalleryContainer.visibility = View.GONE
    }

    private fun showContent() {
        backdropImageContainer.visibility = View.VISIBLE
        genreChipGroup.visibility = View.VISIBLE
        overviewTextView.visibility = View.VISIBLE
        castContainer.visibility = View.VISIBLE
        productionRowTitle.visibility = View.VISIBLE
        productionSeeAllButton.visibility = View.VISIBLE
        directorTitle.visibility = View.VISIBLE
        directorTextView.visibility = View.VISIBLE
        productionCompaniesTitle.visibility = View.VISIBLE
        productionCompaniesTextView.visibility = View.VISIBLE
        filmingLocationsTitle.visibility = View.VISIBLE
        filmingLocationsTextView.visibility = View.VISIBLE
        releaseDateTitle.visibility = View.VISIBLE
        releaseDateTextView.visibility = View.VISIBLE
        runtimeTitle.visibility = View.VISIBLE
        runtimeTextView.visibility = View.VISIBLE
        statusTitle.visibility = View.VISIBLE
        statusTextView.visibility = View.VISIBLE
        budgetTitle.visibility = View.VISIBLE
        budgetTextView.visibility = View.VISIBLE
        performanceTitle.visibility = View.VISIBLE
        revenueTitle.visibility = View.VISIBLE
        revenueTextView.visibility = View.VISIBLE
        ratingTitle.visibility = View.VISIBLE
        ratingTextView.visibility = View.VISIBLE
        posterGalleryContainer.visibility = View.VISIBLE
    }

    // Intended for the preview cast members shown in the fragment
    private fun highlightedCastOnClickListener(personId: Int) {
        navigate(
            R.id.action_movieDetailFragment_to_personDetailFragment,
            PersonDetailArgs(personId)
        )
    }

    private fun castSeeAllOnClickListener(castList: List<CastMemberItem>) {
        navigate(
            R.id.action_movieDetailFragment_to_castFragment,
            CastStateArgs(castList)
        )
    }

    private fun productionSeeAllOnClickListener() {
//        withState(viewModel) { state ->
//            navigate(
//                R.id.action_movieDetailFragment_to_productionDetailFragment,
//                state.movieDetailItem?.let { ProductionDetailStateArgs(it) }
//            )
//        }
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }

    companion object {
        private const val CAST_PREVIEW_VALUE = 6
        private const val POSTER_PREVIEW_VALUE = 8
    }
}
