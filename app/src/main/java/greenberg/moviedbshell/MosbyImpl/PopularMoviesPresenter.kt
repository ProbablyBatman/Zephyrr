package greenberg.moviedbshell.MosbyImpl

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.PopularMovieAdapter
import greenberg.moviedbshell.RetrofitHelpers.RetrofitHelper
import greenberg.moviedbshell.RetrofitHelpers.TMDBService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PopularMoviesPresenter : MvpBasePresenter<PopularMoviesView>() {

    //TODO: revisit this stupid thing
    private var TMDBService: TMDBService = RetrofitHelper().getTMDBService()
    private var isRecyclerLoading = false
    //Default to getting the first page
    private var pageNumber = 1

    fun loadPopularMovies(pullToRefresh: Boolean) {
        pageNumber = 1
        TMDBService.queryPopularMovies(pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response -> ifViewAttached {
                        view: PopularMoviesView ->
                            view.setMovies(response)
                            view.showMovies()
                    }
                }, {
                    throwable -> ifViewAttached {
                        view: PopularMoviesView ->
                            view.showError(throwable, pullToRefresh)
                    }
                })
    }

    fun initRecyclerPagination(recyclerView: RecyclerView?, adapter: PopularMovieAdapter?) {
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
                        ifViewAttached { view: PopularMoviesView ->
                            view.showPageLoad()
                            fetchNextPage()
                        }
                    }
                }
            })
        }
    }

    //Gets next page of popular movies call
    private fun fetchNextPage() {
        TMDBService.queryPopularMovies(++pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response -> ifViewAttached {
                    view: PopularMoviesView ->
                    isRecyclerLoading = true
                    view.addMovies(response)
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