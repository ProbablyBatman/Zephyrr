package greenberg.moviedbshell

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import greenberg.moviedbshell.MosbyImpl.PopularMoviesFragment

class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity_layout)

        if (findViewById<FrameLayout>(R.id.fragment_container) != null) {
            val popularMoviesFragment = PopularMoviesFragment()

            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, popularMoviesFragment)
                    .commit()
        }
    }
/*
    override fun createPresenter(): PopularMoviesPresenter = PopularMoviesPresenter()

    override fun showLoading(pullToRefresh: Boolean) {
        Log.w("Testing", "Loading")
        presenter.loadPopularMovies(pullToRefresh)
    }

    override fun setMovies(response: PopularMovieResponse) {
        Log.w("Testing", "Setting movies")
        popularMovieAdapter.popularMovieList = response.results
        popularMovieAdapter.notifyDataSetChanged()
    }

    override fun showMovies() {
        Log.w("Testing", "Showing movies")
        popularMovieRefresher.isRefreshing = false
    }

    override fun showError(throwable: Throwable, pullToRefresh: Boolean) {
        Log.w("Testing", "Showing error")
        popularMovieRefresher.isRefreshing = false
    }*/
}