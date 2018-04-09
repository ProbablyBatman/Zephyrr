package greenberg.moviedbshell

import android.app.PictureInPictureParams
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.util.Rational
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.Models.MovieResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var movieService: MovieService
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var outputTextView: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var scrollView: NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //outputTextView = findViewById(R.id.output)
        //posterImageView = findViewById(R.id.poster)

        movieService = RetrofitHelper().getMovieService()

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbarLayout.title = "Testing"
        collapsingToolbarLayout.setExpandedTitleColor(resources.getColor(android.R.color.transparent))

        posterImageView = findViewById(R.id.image)
        scrollView = findViewById(R.id.scroll)
        outputTextView = findViewById(R.id.output)

        requestMovie()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun requestMovie() {
        compositeDisposable.add(movieService.queryMovies(550)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    fetchPoster(it)
                    displayInfo(it)
                })
        )
    }

    private fun displayInfo(movieResponse: MovieResponse) {
        Log.d("Hmm", movieResponse.let { "${it.originalTitle}" })
        //outputTextView.text = movieResponse.originalTitle
        outputTextView.text = movieResponse.let { "${it.overview}" }

    }

    private fun fetchPoster(movieResponse: MovieResponse) {
        Log.d("Hmm", movieResponse.let { "${it.backdropPath}" })
        movieResponse.backdropPath?.let {
            Glide.with(this)
                    .load(buildPosterURL(it))
                    .apply { RequestOptions().centerInside() }
                    .into(posterImageView)
        }
        /*movieResponse.posterPath?.let {
            Glide.with(this)
                    .load(buildPosterURL(it))
                    .apply { RequestOptions().centerCrop() }
                    .into(posterImageView)
        }*/
    }

    //TODO: replace this with string resources
    private fun buildPosterURL(endurl: String) = "https://image.tmdb.org/t/p/original$endurl"

    private fun buildBackgroundPosterURL(endurl: String) = "https://image.tmdb.org/t/p/original$endurl"
}
