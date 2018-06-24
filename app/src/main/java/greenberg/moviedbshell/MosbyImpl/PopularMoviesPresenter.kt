package greenberg.moviedbshell.MosbyImpl

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.Models.PopularMoviesModels.PopularMovieResultsItem
import greenberg.moviedbshell.RetrofitHelpers.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PopularMoviesPresenter : MvpBasePresenter<PopularMoviesView>() {

    //TODO: revisit this stupid thing
    private var TMDBService = RetrofitHelper().getTMDBService()
    private var isRecyclerLoading = false
    //Default to getting the first page
    private var popularMoviePageNumber = 1
    private var popularMoviesList = mutableListOf<PopularMovieResultsItem?>()

    fun loadPopularMovies(pullToRefresh: Boolean) {
        popularMoviePageNumber = 1
        TMDBService.queryPopularMovies(popularMoviePageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response -> ifViewAttached {
                        view: PopularMoviesView ->
                            response.results?.map { popularMoviesList.add(it) }
                            view.setMovies(popularMoviesList)
                            view.showMovies()
                    }
                },  {
                    throwable -> ifViewAttached {
                        view: PopularMoviesView ->
                            view.showError(throwable, pullToRefresh)
                    }
                })
    }

    fun initRecyclerPagination(recyclerView: RecyclerView?) {
        recyclerView?.apply {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = this@apply.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isRecyclerLoading
                            && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        ifViewAttached {
                            view: PopularMoviesView ->
                                isRecyclerLoading = true
                                view.showPageLoad()
                                fetchNextPage()
                        }
                    }
                }
            })
        }
    }

    fun onCardSelected(position: Int) {
        val fragment = MovieDetailFragment()
        val bundle = Bundle().apply {
            putInt("MovieID", position)
        }
        fragment.arguments = bundle
        ifViewAttached {
            view: PopularMoviesView ->
                view.showDetail(fragment)
        }
    }

    //Gets next page of popular movies call
    private fun fetchNextPage() {
        TMDBService.queryPopularMovies(++popularMoviePageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response -> ifViewAttached {
                        view: PopularMoviesView ->
                            response.results?.map { popularMoviesList.add(it) }
                            view.addMovies(popularMoviesList)
                            view.hidePageLoad()
                            isRecyclerLoading = false
                    }
                }, {
                    throwable -> ifViewAttached {
                        view: PopularMoviesView ->
                            //todo: revisit erroring the whole page on this.  Just error bottom
                            view.showError(throwable, false)
                    }
                })
    }

    override fun detachView() {
        super.detachView()
        //TODO: cancel anything going on here, though press x to doubt
    }
}