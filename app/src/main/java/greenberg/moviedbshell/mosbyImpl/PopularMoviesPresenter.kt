package greenberg.moviedbshell.mosbyImpl

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.models.PopularMoviesModels.PopularMovieResultsItem
import greenberg.moviedbshell.R
import greenberg.moviedbshell.retrofitHelpers.RetrofitHelper
import greenberg.moviedbshell.viewHolders.PopularMovieAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat

class PopularMoviesPresenter : MvpBasePresenter<PopularMoviesView>() {

    //TODO: revisit this stupid thing
    private var TMDBService = RetrofitHelper().getTMDBService()
    private var isRecyclerLoading = false
    //Default to getting the first page
    private var popularMoviePageNumber = 1
    private var popularMoviesList = mutableListOf<PopularMovieResultsItem?>()

    override fun attachView(view: PopularMoviesView) {
        super.attachView(view)
        initView()
    }

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

    private fun initView() {
        ifViewAttached {
            view: PopularMoviesView ->
                view.showLoading(false)
        }
    }

    fun refreshPage(adapter: PopularMovieAdapter?) {
        ifViewAttached {
            view: PopularMoviesView ->
                view.showLoading(true)
                adapter?.popularMovieList?.clear()
                adapter?.notifyDataSetChanged()
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
                            view.setMovies(popularMoviesList)
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

    fun fetchPosterArt(cardItemPosterView: ImageView, item: PopularMovieResultsItem) {
        //Load poster art
        item.posterPath?.let {
            Glide.with(cardItemPosterView)
                    //TODO: potentially hacky way to get context
                    .load(cardItemPosterView.context.getString(R.string.poster_url_substitution, it))
                    .apply { RequestOptions().centerCrop() }
                    .into(cardItemPosterView)
        }
    }

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

    override fun detachView() {
        super.detachView()
        //TODO: cancel anything going on here, though press x to doubt
    }
}