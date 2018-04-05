package greenberg.moviedbshell

import android.app.PictureInPictureParams
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        outputTextView = findViewById(R.id.output)
        posterImageView = findViewById(R.id.poster)

        movieService = RetrofitHelper().getMovieService()

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
        outputTextView.text = movieResponse.originalTitle
    }

    private fun fetchPoster(movieResponse: MovieResponse) {
        Log.d("Hmm", movieResponse.let { "${it.posterPath}" })
        movieResponse.posterPath?.let {
            Glide.with(this)
                    .load(buildPosterURL(it))
                    .apply { RequestOptions().centerCrop() }
                    .into(posterImageView)
        }
    }

    //TODO: replace this with string resources
    private fun buildPosterURL(endurl: String) = "https://image.tmdb.org/t/p/original/$endurl"
}
