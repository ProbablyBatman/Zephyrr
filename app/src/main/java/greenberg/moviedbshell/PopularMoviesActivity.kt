package greenberg.moviedbshell

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import greenberg.moviedbshell.Models.PopularMoviesModels.PopularMovieResponse
import greenberg.moviedbshell.RetrofitHelpers.TMDBService
import greenberg.moviedbshell.RetrofitHelpers.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

//TODO: probably going to have to handle overall font sizing for the app in terms of accessibility and such.
class PopularMoviesActivity: AppCompatActivity() {
    private lateinit var TMDBService: TMDBService
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var cardItemPosterImage: ImageView
    private lateinit var cardItemTitle: TextView
    private lateinit var cardItemReleaseDate: TextView
    private lateinit var cardItemOverview: TextView
    private lateinit var popularMovieRecycler: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var popularMovieAdapter: PopularMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popular_movies_activity)

        TMDBService = RetrofitHelper().getTMDBService()

        popularMovieRecycler = findViewById(R.id.popularMovieRecycler)

        //TODO: look into proper context for this; i.e. application or base
        linearLayoutManager = LinearLayoutManager(this)
        popularMovieRecycler.layoutManager = linearLayoutManager

        requestPopularMovies()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun requestPopularMovies() {
        compositeDisposable.add(TMDBService.queryPopularMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it -> createAdapter(it) }))
    }

    private fun createAdapter(response: PopularMovieResponse) {
        popularMovieAdapter = PopularMovieAdapter(response.results)
        popularMovieRecycler.adapter = popularMovieAdapter
    }

}