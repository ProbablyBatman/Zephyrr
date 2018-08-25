package greenberg.moviedbshell.presenters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.R
import greenberg.moviedbshell.mappers.MovieDetailMapper
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.view.MovieDetailView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MovieDetailPresenter
@Inject constructor(private val TMDBService: TMDBService,
                    private val context: Context,
                    private val mapper: MovieDetailMapper) : MvpBasePresenter<MovieDetailView>() {

    private var compositeDisposable = CompositeDisposable()

    override fun attachView(view: MovieDetailView) {
        super.attachView(view)
        Timber.d("attachView")
    }

    fun initView(movieId: Int) {
        ifViewAttached { view: MovieDetailView ->
            view.showLoading(movieId)
        }
    }

    fun loadMovieDetails(movieId: Int) {
        val disposable =
                TMDBService.queryMovieDetail(movieId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            ifViewAttached { view: MovieDetailView ->
                                view.showMovieDetails(mapper.mapToEntity(response))
                            }
                        }, { throwable ->
                            ifViewAttached { view: MovieDetailView ->
                                view.showError(throwable)
                            }
                        })
        compositeDisposable.add(disposable)
    }

    //TODO: consider moving these utility functions to a presenter superclass for details
    //these are used multiple times
    fun fetchPosterArt(glide: RequestManager, view: ImageView?, posterUrl: String) {
        Timber.d("posterURL: $posterUrl")
        if (posterUrl.isNotEmpty() && view != null) {
            val validUrl = context.getString(R.string.poster_url_substitution, posterUrl)
            //Load image into view
            glide.load(validUrl)
                    .apply {
                        RequestOptions()
                                .placeholder(ColorDrawable(Color.DKGRAY))
                                .fallback(ColorDrawable(Color.DKGRAY))
                                .centerCrop()
                    }
                    .into(view)
        } else {
            Timber.d("Poster url is null for movie detail")
            //TODO: look into placeholders
        }
    }

    //TODO: probably make sure every date is like this?
    //there has to be a better way to do this
    fun processReleaseDate(releaseDate: String): String {
        return if (releaseDate.isNotBlank()) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(releaseDate)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            outputFormat.format(date)
        } else {
            ""
        }
    }

    fun processRatingInfo(voteAverage: Double, voteCount: Int): String {
        val formattedAverage: String = formatRatings(voteAverage)
        return context.getString(R.string.user_rating_substitution, formattedAverage, voteCount)
    }

    private fun formatRatings(voteAverage: Double?): String {
        val doubleFormat: NumberFormat = DecimalFormat("##.##")
        return doubleFormat.format(voteAverage)
    }

    fun processRuntime(runtime: Int): String = context.getString(R.string.runtime_substitution, runtime.toString())

    fun processGenreTitle(genresListSize: Int): String = context.resources.getQuantityString(R.plurals.genres_bold, genresListSize)

    fun processGenres(genres: List<String?>): String = genres.joinToString(", ")

    override fun destroy() {
        super.destroy()
        Timber.d("destroy called, disposables disposed of")
        compositeDisposable.dispose()
    }
}
