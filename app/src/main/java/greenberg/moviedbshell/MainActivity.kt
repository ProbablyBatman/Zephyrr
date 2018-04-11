package greenberg.moviedbshell

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.Models.MovieResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import android.support.design.widget.AppBarLayout
import java.text.DecimalFormat
import java.text.NumberFormat


class MainActivity : AppCompatActivity() {

    private lateinit var movieService: MovieService
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var posterImageView: ImageView
    private lateinit var backdropImageView: ImageView
    private lateinit var appBar: AppBarLayout
    private lateinit var collapsingToolbarLayout: net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
    private lateinit var scrollView: NestedScrollView

    private lateinit var releaseDateTextView: TextView
    private lateinit var ratingTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var overviewTextView: TextView
    private lateinit var runtimeTextView: TextView
    private lateinit var genresTitleTextView: TextView
    private lateinit var genresTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        movieService = RetrofitHelper().getMovieService()

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        appBar = findViewById(R.id.app_bar_layout)

        posterImageView = findViewById(R.id.posterImage)
        backdropImageView = findViewById(R.id.backdropImage)
        scrollView = findViewById(R.id.scroll)
        overviewTextView = findViewById(R.id.overview)
        releaseDateTextView = findViewById(R.id.release_date)
        ratingTextView = findViewById(R.id.user_rating)
        statusTextView = findViewById(R.id.status)
        runtimeTextView = findViewById(R.id.runtime)
        genresTitleTextView = findViewById(R.id.genres_bold)
        genresTextView = findViewById(R.id.genres)

        requestMovie()
        //TODO: look into image and video response
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun requestMovie() {
        compositeDisposable.add(movieService.queryMovies(11576)//338970)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    fetchPosters(it)
                    displayInfo(it)
                })
        )
    }

    private fun displayInfo(movieResponse: MovieResponse) {
        Log.d("Hmm", movieResponse.originalTitle)
        collapsingToolbarLayout.title = movieResponse.originalTitle
        overviewTextView.text = movieResponse.overview

        //todo: process output
        releaseDateTextView.text = movieResponse.releaseDate?.let { processReleaseDate(it) }

        val doubleFormat: NumberFormat = DecimalFormat("##.##")
        ratingTextView.text = movieResponse.let { resources.getString(R.string.user_rating_substitution, doubleFormat.format(it.voteAverage), it.voteCount) }
        statusTextView.text = movieResponse.status
        runtimeTextView.text = movieResponse.let { resources.getString(R.string.runtime_substitution, it.runtime) }
        //Default to One
        genresTitleTextView.text = resources.getQuantityString(R.plurals.genres_bold, movieResponse.genres?.size ?: 1)
        genresTextView.text = movieResponse.let { "${it.genres
                ?.map { it?.name }
                ?.joinToString(", ")
            }"
        }


        //TODO: potentially scrape other rating information
    }

    private fun fetchPosters(movieResponse: MovieResponse) {
        Log.d("Hmm", movieResponse.let { "${it.backdropPath}" })
        //Load backdrop image
        movieResponse.backdropPath?.let {
            Glide.with(this)
                    .load(buildImageURL(it))
                    .into(backdropImageView)
        }

        //Load poster art
        movieResponse.posterPath?.let {
            Glide.with(this)
                    .load(buildImageURL(it))
                    .apply { RequestOptions().centerCrop() }
                    .into(posterImageView)
        }
    }

    //TODO: replace this with string resources
    private fun buildImageURL(endurl: String) = "https://image.tmdb.org/t/p/original$endurl"

    //TODO: probably make sure every date is like this?
    //there has to be a better way to do this
    private fun processReleaseDate(releaseDate: String): String {
        val splitDate = releaseDate.split("-")
        return "${splitDate[1]}/${splitDate[2]}/${splitDate[0]}"
    }
}
