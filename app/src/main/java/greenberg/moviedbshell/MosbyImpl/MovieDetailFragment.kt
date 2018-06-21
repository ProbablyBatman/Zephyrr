package greenberg.moviedbshell.MosbyImpl

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import greenberg.moviedbshell.Models.MovieDetailModels.MovieDetailResponse
import greenberg.moviedbshell.R
import java.text.DecimalFormat
import java.text.NumberFormat

class MovieDetailFragment :
        MvpFragment<MovieDetailView, MovieDetailPresenter>(),
        MovieDetailView {

    private var posterImageView: ImageView? = null
    private var backdropImageView: ImageView? = null
    private var appBar: AppBarLayout? = null
    private var collapsingToolbarLayout: net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout? = null
    private var scrollView: NestedScrollView? = null

    private var releaseDateTextView: TextView? = null
    private var ratingTextView: TextView? = null
    private var statusTextView: TextView? = null
    private var overviewTextView: TextView? = null
    private var runtimeTextView: TextView? = null
    private var genresTitleTextView: TextView? = null
    private var genresTextView: TextView? = null

    private var movieId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            movieId = arguments?.get("MovieID") as Int
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_detail_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collapsingToolbarLayout = activity?.findViewById(R.id.collapsing_toolbar)
        appBar = activity?.findViewById(R.id.app_bar_layout)

        posterImageView = activity?.findViewById(R.id.posterImage)
        backdropImageView = activity?.findViewById(R.id.backdropImage)
        scrollView = activity?.findViewById(R.id.scroll)
        overviewTextView = activity?.findViewById(R.id.overview)
        releaseDateTextView = activity?.findViewById(R.id.release_date)
        ratingTextView = activity?.findViewById(R.id.user_rating)
        statusTextView = activity?.findViewById(R.id.status)
        runtimeTextView = activity?.findViewById(R.id.runtime)
        genresTitleTextView = activity?.findViewById(R.id.genres_bold)
        genresTextView = activity?.findViewById(R.id.genres)

        //activity?.setActionBar(collapsingToolbarLayout)

        showLoading(movieId)
    }

    override fun createPresenter(): MovieDetailPresenter = MovieDetailPresenter()

    override fun showLoading(movieId: Int) {
        Log.w("Testing", "Show Loading")
        presenter.loadMovieDetails(movieId)
    }

    override fun showError(throwable: Throwable) {
        Log.w("Testing", "Showing Error")
        Log.w("Throwable", throwable)
    }

    override fun showMovieDetails(movieDetailResponse: MovieDetailResponse) {
        Log.w("Testing", "Showing Movie Details")

        //TODO: require context, is this good?
        presenter.fetchPoster(requireContext(), posterImageView!!,  movieDetailResponse.posterPath)
        presenter.fetchPoster(requireContext(), backdropImageView!!, movieDetailResponse.backdropPath)

        collapsingToolbarLayout?.title = movieDetailResponse.originalTitle
        overviewTextView?.text = movieDetailResponse.overview

        //todo: process output
        releaseDateTextView?.text = movieDetailResponse.releaseDate?.let { presenter.processReleaseDate(it) }

        val doubleFormat: NumberFormat = DecimalFormat("##.##")
        ratingTextView?.text = movieDetailResponse.let { resources.getString(R.string.user_rating_substitution, doubleFormat.format(it.voteAverage), it.voteCount) }
        statusTextView?.text = movieDetailResponse.status
        runtimeTextView?.text = movieDetailResponse.let { resources.getString(R.string.runtime_substitution, it.runtime) }
        //Default to One
        genresTitleTextView?.text = resources.getQuantityString(R.plurals.genres_bold, movieDetailResponse.genres?.size ?: 1)
        genresTextView?.text = movieDetailResponse.let {
            "${it.genres
                ?.map { it?.name }
                ?.joinToString(", ")}"
        }
        //TODO: potentially scrape other rating information
    }

    companion object {
        @JvmField val TAG: String = MovieDetailFragment::class.java.simpleName
    }
}