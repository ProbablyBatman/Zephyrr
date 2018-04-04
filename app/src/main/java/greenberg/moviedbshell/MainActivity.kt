package greenberg.moviedbshell

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import greenberg.moviedbshell.Models.MovieResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var movieService: MovieService
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var outputTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        outputTextView = findViewById(R.id.output)

        movieService = RetrofitHelper().getMovieService()

        requestMovie()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun requestMovie() {
        //Asynchronus, no rx to make this possible rn
        /*movieService.queryMovies(550).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>?, response: Response<MovieResponse>?) {

            }

            override fun onFailure(call: Call<MovieResponse>?, t: Throwable?) {

            }
        })*/

        //Other way
        /*movieService.queryMovies(550).execute().body()?.let {
            outputTextView.text = it.originalTitle
        }*/

        compositeDisposable.add(movieService.queryMovies(550)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    displayInfo(it)
                })
        )
    }

    private fun displayInfo(movieResponse: MovieResponse) {
        Log.d("Hmm", movieResponse.let { "${it.originalTitle} Yeah Just kill me" })
        outputTextView.text = movieResponse.originalTitle
    }
}
