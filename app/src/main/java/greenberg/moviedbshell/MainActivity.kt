package greenberg.moviedbshell

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
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
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var movieService: MovieService
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var outputTextView: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var backdropImageView: ImageView
    private lateinit var appBar: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var scrollView: NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //outputTextView = findViewById(R.id.output)

        movieService = RetrofitHelper().getMovieService()

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        appBar = findViewById(R.id.app_bar_layout)
        //collapsingToolbarLayout.setExpandedTitleColor(resources.getColor(android.R.color.transparent))

        appBar.addOnOffsetChangedListener({ appBarLayout, verticalOffset ->
            posterImageView.alpha = 1.0f - Math.abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())
        })

        posterImageView = findViewById(R.id.posterImage)
        backdropImageView = findViewById(R.id.backdropImage)
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
                    fetchPosters(it)
                    displayInfo(it)
                })
        )
    }

    private fun displayInfo(movieResponse: MovieResponse) {
        Log.d("Hmm", movieResponse.let { "${it.originalTitle}" })
        collapsingToolbarLayout.title = movieResponse.let { it.originalTitle }
        //outputTextView.text = movieResponse.originalTitle
        outputTextView.text = movieResponse.let {
            "${it.overview} ${it.overview} ${it.overview} ${it.overview} ${it.overview} ${it.overview}" +
                    " ${it.overview}\n ${it.overview} ${it.overview}\n ${it.overview} ${it.overview} ${it.overview}" +
                    " ${it.overview}\n ${it.overview} ${it.overview}\n ${it.overview} ${it.overview} ${it.overview}"
        }
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
}
