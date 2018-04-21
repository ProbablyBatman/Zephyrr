package greenberg.moviedbshell

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import greenberg.moviedbshell.Models.PopularMoviesModels.PopularMovieResponse
import greenberg.moviedbshell.MosbyImpl.PopularMoviesPresenter
import greenberg.moviedbshell.MosbyImpl.PopularMoviesView

class BaseActivity :
        MvpActivity<PopularMoviesView, PopularMoviesPresenter>(),
        PopularMoviesView,
        SwipeRefreshLayout.OnRefreshListener {

    private lateinit var popularMovieRecycler: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var popularMovieAdapter: PopularMovieAdapter
    private lateinit var popularMovieRefresher: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popular_movies_activity)

        //TODO: hmmmm
        /*if (findViewById<FrameLayout>(R.id.fragment_container) != null) {
            val popularMoviesFragment = PopularMoviesFragment()

            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, popularMoviesFragment)
                    .commit()
        }*/

        popularMovieRecycler = findViewById(R.id.popularMovieRecycler)
        popularMovieRefresher = findViewById(R.id.popularMovieRefresher)

        //TODO: look into proper context for this; i.e. application or base
        linearLayoutManager = LinearLayoutManager(this)
        popularMovieRecycler.layoutManager = linearLayoutManager
        //TODO: revisit this initialization
        popularMovieAdapter = PopularMovieAdapter(null)
        popularMovieRecycler.adapter = popularMovieAdapter

        showLoading(false)
    }

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
    }

    override fun onRefresh() {
        showLoading(true)
    }
}