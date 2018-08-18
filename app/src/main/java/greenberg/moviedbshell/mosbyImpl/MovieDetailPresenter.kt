package greenberg.moviedbshell.mosbyImpl

import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.retrofitHelpers.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MovieDetailPresenter : MvpBasePresenter<MovieDetailView>() {

    private var TMDBService = RetrofitHelper().getTMDBService()
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
                TMDBService.queryMovies(movieId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            ifViewAttached { view: MovieDetailView ->
                                view.showMovieDetails(response)
                            }
                        }, { throwable ->
                            ifViewAttached { view: MovieDetailView ->
                                view.showError(throwable)
                            }
                        })
        compositeDisposable.add(disposable)
    }

    /*TODO: Utility functions are bad, but idk what to do with these */
    fun fetchPosterArt(glide: RequestManager, view: ImageView, posterURL: String?) {
        Timber.d("posterURL: $posterURL")
        if (posterURL != null) {
            //Load image into view
            glide.load(posterURL).into(view)
        } else {
            Timber.d("Poster url is null for ${view.id}")
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

    //TODO: handle case where data like this is missing
    fun processRatings(voteAverage: Double?): String? {
        val doubleFormat: NumberFormat = DecimalFormat("##.##")
        return doubleFormat.format(voteAverage)
    }

    override fun destroy() {
        super.destroy()
        Timber.d("destroy called, disposables disposed of")
        compositeDisposable.dispose()
    }
}
