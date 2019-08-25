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
import greenberg.moviedbshell.models.ui.MovieDetailItem
import greenberg.moviedbshell.presenters.MovieDetailPresenter
import greenberg.moviedbshell.processReleaseDate
import greenberg.moviedbshell.viewHolders.CastListAdapter
import timber.log.Timber

class MovieDetailFragment :
        BaseFragment<MovieDetailView, MovieDetailPresenter>(),
        MovieDetailView {

    private var progressBar: ProgressBar? = null
    private var posterImageContainer: FrameLayout? = null
    private var posterImageView: ImageView? = null
    private var backdropImageView: ImageView? = null
    private var backdropImageContainer: FrameLayout? = null
    private var titleBar: TextView? = null
    private var scrollView: NestedScrollView? = null

    private var releaseDateTextView: TextView? = null
    private var releaseDateTitle: TextView? = null
    private var ratingTextView: TextView? = null
    private var ratingTitle: TextView? = null
    private var statusTextView: TextView? = null
    private var statusTitle: TextView? = null
    private var overviewTextView: TextView? = null
    private var runtimeTextView: TextView? = null
    private var runtimeTitle: TextView? = null
    private var genresTitle: TextView? = null
    private var genresTextView: TextView? = null
    private var castRecyclerView: RecyclerView? = null
    private var errorTextView: TextView? = null
    private var errorRetryButton: MaterialButton? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var castListAdapter: CastListAdapter

    private var movieId = -1
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        if (arguments != null) {
            movieId = arguments?.get("MovieID") as? Int ?: -1
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_detail_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.movie_detail_progress_bar)
        posterImageContainer = view.findViewById(R.id.movie_detail_poster_container)
        backdropImageContainer = view.findViewById(R.id.movie_detail_background_image_container)
        titleBar = view.findViewById(R.id.movie_detail_title)
        posterImageView = view.findViewById(R.id.movie_detail_poster)
        backdropImageView = view.findViewById(R.id.movie_detail_background_image)
        scrollView = view.findViewById(R.id.movie_detail_scroll)
        overviewTextView = view.findViewById(R.id.movie_detail_overview)
        releaseDateTextView = view.findViewById(R.id.movie_detail_release_date)
        releaseDateTitle = view.findViewById(R.id.movie_detail_release_date_title)
        ratingTextView = view.findViewById(R.id.movie_detail_user_rating)
        ratingTitle = view.findViewById(R.id.movie_detail_user_rating_title)
        statusTextView = view.findViewById(R.id.movie_detail_status)
        statusTitle = view.findViewById(R.id.movie_detail_status_title)
        runtimeTextView = view.findViewById(R.id.movie_detail_runtime)
        runtimeTitle = view.findViewById(R.id.movie_detail_runtime_title)
        genresTitle = view.findViewById(R.id.movie_detail_genres_title)
        genresTextView = view.findViewById(R.id.movie_detail_genres)
        errorTextView = view.findViewById(R.id.movie_detail_error)
        errorRetryButton = view.findViewById(R.id.movie_detail_retry_button)
        castRecyclerView = view.findViewById(R.id.movie_detail_cast_members_recycler)

        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        castRecyclerView?.layoutManager = linearLayoutManager
        castListAdapter = CastListAdapter()
        castRecyclerView?.adapter = castListAdapter
        presenter?.initView(movieId, castListAdapter)
        presenter.loadMovieDetails(movieId)
        navController = findNavController()
    }

    override fun createPresenter(): MovieDetailPresenter = presenter
            ?: (activity?.application as ZephyrrApplication).component.movieDetailPresenter()

    override fun showLoading(movieId: Int) {
        Timber.d("Show Loading")
        hideAllViews()
        hideErrorState()
        showLoadingBar()
    }

    override fun showError(throwable: Throwable) {
        Timber.d("Showing Error")
        Timber.e(throwable)
        hideLoadingBar()
        showErrorState()
        errorRetryButton?.setOnClickListener {
            presenter.loadMovieDetails(movieId)
            hideErrorState()
        }
    }

    override fun showMovieDetails(movieDetailItem: MovieDetailItem) {
        Timber.d("Showing Movie Details")

        Timber.d("posterURL: ${movieDetailItem.posterImageUrl}")
        if (movieDetailItem.posterImageUrl.isNotEmpty() && posterImageView != null) {
            val validUrl = resources.getString(R.string.poster_url_substitution, movieDetailItem.posterImageUrl)
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

        Timber.d("backdropURL: ${movieDetailItem.backdropImageUrl}")
        if (movieDetailItem.posterImageUrl.isNotEmpty() && backdropImageView != null) {
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
                    .into(backdropImageView!!)
        }

        titleBar?.text = movieDetailItem.movieTitle
        overviewTextView?.text = movieDetailItem.overview
        releaseDateTextView?.text = processReleaseDate(movieDetailItem.releaseDate)
        ratingTextView?.text = presenter.processRatingInfo(movieDetailItem.voteAverage, movieDetailItem.voteCount)
        statusTextView?.text = movieDetailItem.status
        runtimeTextView?.text = presenter.processRuntime(movieDetailItem.runtime)
        genresTitle?.text = presenter.processGenreTitle(movieDetailItem.genres.size)
        genresTextView?.text = presenter.processGenres(movieDetailItem.genres)
        // TODO: potentially scrape other rating information
        hideLoadingBar()
        showAllViews()
    }

    override fun showDetail(bundle: Bundle) {
        navController?.navigate(R.id.action_movieDetailFragment_to_personDetailFragment, bundle)
    }

    private fun hideAllViews() {
        progressBar?.visibility = View.GONE
        titleBar?.visibility = View.GONE
        posterImageContainer?.visibility = View.GONE
        scrollView?.visibility = View.GONE
        overviewTextView?.visibility = View.GONE
        releaseDateTextView?.visibility = View.GONE
        ratingTextView?.visibility = View.GONE
        statusTextView?.visibility = View.GONE
        runtimeTextView?.visibility = View.GONE
        genresTextView?.visibility = View.GONE
        backdropImageContainer?.visibility = View.GONE
        genresTitle?.visibility = View.GONE
        statusTitle?.visibility = View.GONE
        releaseDateTitle?.visibility = View.GONE
        ratingTitle?.visibility = View.GONE
        runtimeTitle?.visibility = View.GONE
        castRecyclerView?.visibility = View.GONE
    }

    private fun showAllViews() {
        titleBar?.visibility = View.VISIBLE
        posterImageContainer?.visibility = View.VISIBLE
        scrollView?.visibility = View.VISIBLE
        overviewTextView?.visibility = View.VISIBLE
        releaseDateTextView?.visibility = View.VISIBLE
        ratingTextView?.visibility = View.VISIBLE
        statusTextView?.visibility = View.VISIBLE
        runtimeTextView?.visibility = View.VISIBLE
        genresTextView?.visibility = View.VISIBLE
        backdropImageContainer?.visibility = View.VISIBLE
        genresTitle?.visibility = View.VISIBLE
        statusTitle?.visibility = View.VISIBLE
        releaseDateTitle?.visibility = View.VISIBLE
        ratingTitle?.visibility = View.VISIBLE
        runtimeTitle?.visibility = View.VISIBLE
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

    companion object {
        @JvmField
        val TAG: String = MovieDetailFragment::class.java.simpleName
    }
}
