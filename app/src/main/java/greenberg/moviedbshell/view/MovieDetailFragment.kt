package greenberg.moviedbshell.view

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.models.moviedetailmodels.MovieDetailResponse
import greenberg.moviedbshell.models.ui.MovieDetailItem
import greenberg.moviedbshell.presenters.MovieDetailPresenter
import timber.log.Timber

class MovieDetailFragment :
        MvpFragment<MovieDetailView, MovieDetailPresenter>(),
        MovieDetailView {

    private var progressBar: ProgressBar? = null
    private var posterImageView: ImageView? = null
    private var backdropImageView: ImageView? = null
    private var backdropImageWrapper: FrameLayout? = null
    private var appBar: AppBarLayout? = null
    private var collapsingToolbarLayout: net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout? = null
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

    private var movieId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        if (arguments != null) {
            movieId = arguments?.get("MovieID") as Int
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_detail_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")

        //collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar)
        //appBar = view.findViewById(R.id.app_bar_layout)
        progressBar = view.findViewById(R.id.movieDetailProgressBar)
        backdropImageWrapper = view.findViewById(R.id.backgroundImageWrapper)
        titleBar = view.findViewById(R.id.movie_detail_title)
        posterImageView = view.findViewById(R.id.posterImage)
        backdropImageView = view.findViewById(R.id.backdropImage)
        scrollView = view.findViewById(R.id.scroll)
        overviewTextView = view.findViewById(R.id.overview)
        releaseDateTextView = view.findViewById(R.id.release_date)
        releaseDateTitle = view.findViewById(R.id.release_date_bold)
        ratingTextView = view.findViewById(R.id.user_rating)
        ratingTitle = view.findViewById(R.id.user_rating_bold)
        statusTextView = view.findViewById(R.id.status)
        statusTitle = view.findViewById(R.id.status_bold)
        runtimeTextView = view.findViewById(R.id.runtime)
        runtimeTitle = view.findViewById(R.id.runtime_bold)
        genresTitle = view.findViewById(R.id.genres_bold)
        genresTextView = view.findViewById(R.id.genres)

        presenter?.initView(movieId)
    }

    override fun createPresenter(): MovieDetailPresenter = presenter
            ?: (activity?.application as ZephyrrApplication).component.movieDetailPresenter()

    override fun showLoading(movieId: Int) {
        Timber.d("Show Loading")
        hideAllViews()
        toggleLoadingBar()
        presenter.loadMovieDetails(movieId)
    }

    override fun showError(throwable: Throwable) {
        Timber.d("Showing Error")
        Timber.d(throwable)
    }

    override fun showMovieDetails(movieDetailItem: MovieDetailItem) {
        Timber.d("Showing Movie Details")

        presenter.fetchPosterArt(Glide.with(this), posterImageView, movieDetailItem.posterImageUrl)
        presenter.fetchPosterArt(Glide.with(this), backdropImageView, movieDetailItem.backdropImageUrl)

        //collapsingToolbarLayout?.title = movieDetailResponse.originalTitle
        titleBar?.text = movieDetailItem.movieTitle
        overviewTextView?.text = movieDetailItem.overview
        releaseDateTextView?.text = presenter.processReleaseDate(movieDetailItem.releaseDate)
        ratingTextView?.text = presenter.processRatingInfo(movieDetailItem.voteAverage, movieDetailItem.voteCount)
        statusTextView?.text = movieDetailItem.status
        runtimeTextView?.text = presenter.processRuntime(movieDetailItem.runtime)
        genresTitle?.text = presenter.processGenreTitle(movieDetailItem.genres.size)
        genresTextView?.text = presenter.processGenres(movieDetailItem.genres)
        //TODO: potentially scrape other rating information
        toggleLoadingBar()
        showAllViews()
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }

    override fun onPause() {
        Timber.d("onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.d("onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
    }

    private fun hideAllViews() {
        titleBar?.visibility = View.GONE
        posterImageView?.visibility = View.GONE
        backdropImageView?.visibility = View.GONE
        scrollView?.visibility = View.GONE
        overviewTextView?.visibility = View.GONE
        releaseDateTextView?.visibility = View.GONE
        ratingTextView?.visibility = View.GONE
        statusTextView?.visibility = View.GONE
        runtimeTextView?.visibility = View.GONE
        genresTextView?.visibility = View.GONE
        backdropImageWrapper?.visibility = View.GONE
        genresTitle?.visibility = View.GONE
        statusTitle?.visibility = View.GONE
        releaseDateTitle?.visibility = View.GONE
        ratingTitle?.visibility = View.GONE
        runtimeTitle?.visibility = View.GONE
    }

    private fun showAllViews() {
        titleBar?.visibility = View.VISIBLE
        posterImageView?.visibility = View.VISIBLE
        backdropImageView?.visibility = View.VISIBLE
        scrollView?.visibility = View.VISIBLE
        overviewTextView?.visibility = View.VISIBLE
        releaseDateTextView?.visibility = View.VISIBLE
        ratingTextView?.visibility = View.VISIBLE
        statusTextView?.visibility = View.VISIBLE
        runtimeTextView?.visibility = View.VISIBLE
        genresTextView?.visibility = View.VISIBLE
        backdropImageWrapper?.visibility = View.VISIBLE
        genresTitle?.visibility = View.VISIBLE
        statusTitle?.visibility = View.VISIBLE
        releaseDateTitle?.visibility = View.VISIBLE
        ratingTitle?.visibility = View.VISIBLE
        runtimeTitle?.visibility = View.VISIBLE
    }

    private fun toggleLoadingBar() {
        progressBar?.visibility =
                if (progressBar?.visibility == View.GONE) View.VISIBLE
                else View.GONE
    }

    companion object {
        @JvmField
        val TAG: String = MovieDetailFragment::class.java.simpleName
    }
}
