package greenberg.moviedbshell.MosbyImpl

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.R
import greenberg.moviedbshell.RetrofitHelpers.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat

class MovieDetailPresenter : MvpBasePresenter<MovieDetailView>() {

    private var TMDBService = RetrofitHelper().getTMDBService()

    fun loadMovieDetails(movieId: Int) {
        TMDBService.queryMovies(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                        response -> ifViewAttached {
                            view: MovieDetailView ->
                                view.showMovieDetails(response)
                        }
                    }, {
                        throwable -> ifViewAttached {
                            view: MovieDetailView ->
                                view.showError(throwable)
                    }
                })
    }

    /*TODO: Utility functions are bad, but idk what to do with these */
    //TODO: is this a good way to get context
    fun fetchPoster(context: Context, view: ImageView, posterURL: String?) {
        Log.d("Poster url", posterURL)
        if (posterURL != null) {
            //Load image into view
            Glide.with(context)
                    .load(context.getString(R.string.poster_url_substitution, posterURL))
                    .into(view)
        } else {
            Log.d("Testing", "Poster url is null for ${view.id}")
            //TODO: look into placeholders
        }
    }

    //TODO: probably make sure every date is like this?
    //there has to be a better way to do this
    fun processReleaseDate(releaseDate: String): String {
        return if (releaseDate.isNotBlank()) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = inputFormat.parse(releaseDate)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy")
            outputFormat.format(date)
        } else {
            return ""
        }
    }
}