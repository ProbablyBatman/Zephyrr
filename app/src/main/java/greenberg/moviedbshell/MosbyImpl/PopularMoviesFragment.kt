package greenberg.moviedbshell.MosbyImpl

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import greenberg.moviedbshell.Models.PopularMoviesModels.PopularMovieResponse
import greenberg.moviedbshell.PopularMovieAdapter
import greenberg.moviedbshell.R

class PopularMoviesFragment :
        MvpFragment<PopularMoviesView, PopularMoviesPresenter>(),
        PopularMoviesView,
        SwipeRefreshLayout.OnRefreshListener {

    private var popularMovieRecycler: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var popularMovieAdapter: PopularMovieAdapter? = null
    private var popularMovieRefresher: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.popular_movies_activity, container, false)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        popularMovieRecycler = activity?.findViewById(R.id.popularMovieRecycler)
        popularMovieRefresher = activity?.findViewById(R.id.popularMovieRefresher)

        //TODO: look into proper context for this; i.e. application or base
        linearLayoutManager = LinearLayoutManager(activity)
        popularMovieRecycler?.layoutManager = linearLayoutManager
        popularMovieRecycler?.adapter = popularMovieAdapter

        showLoading(false)
    }

    override fun createPresenter(): PopularMoviesPresenter = PopularMoviesPresenter()

    override fun showLoading(pullToRefresh: Boolean) {
        presenter.loadPopularMovies(pullToRefresh)
    }

    override fun setMovies(response: PopularMovieResponse) {
        popularMovieAdapter?.popularMovieList = response.results
        popularMovieAdapter?.notifyDataSetChanged()
    }

    override fun showMovies() {
        popularMovieRefresher?.isRefreshing = false
    }

    override fun showError(throwable: Throwable, pullToRefresh: Boolean) {
        popularMovieRefresher?.isRefreshing = false
    }

    override fun onRefresh() {
        showLoading(true)
    }

}