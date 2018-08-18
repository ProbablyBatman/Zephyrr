package greenberg.moviedbshell.mosbyImpl

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.PopularMoviesModels.PopularMovieResultsItem
import greenberg.moviedbshell.retrofitHelpers.RetrofitHelper
import greenberg.moviedbshell.viewHolders.PopularMovieAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class PopularMoviesPresenter : MvpBasePresenter<PopularMoviesView>() {

    //TODO: revisit this stupid thing
    private var TMDBService = RetrofitHelper().getTMDBService()
    private var isRecyclerLoading = false
    //Default to getting the first page
    private var popularMoviePageNumber = 1
    private var popularMoviesList = mutableListOf<PopularMovieResultsItem?>()
    private var compositeDisposable = CompositeDisposable()

    override fun attachView(view: PopularMoviesView) {
        super.attachView(view)
        Timber.d("attachView")
        initView()
    }

    fun loadPopularMovies(pullToRefresh: Boolean) {
        popularMoviePageNumber = 1
        val disposable =
                TMDBService.queryPopularMovies(popularMoviePageNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            ifViewAttached { view: PopularMoviesView ->
                                response.results?.map { popularMoviesList.add(it) }
                                view.setMovies(popularMoviesList)
                                view.showMovies()
                            }
                        }, { throwable ->
                            ifViewAttached { view: PopularMoviesView ->
                                view.showError(throwable, pullToRefresh)
                            }
                        })
        compositeDisposable.add(disposable)
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
                        ifViewAttached { view: PopularMoviesView ->
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
        ifViewAttached { view: PopularMoviesView ->
            view.showLoading(false)
        }
    }

    fun refreshPage(adapter: PopularMovieAdapter?) {
        ifViewAttached { view: PopularMoviesView ->
            view.showLoading(true)
            adapter?.popularMovieList?.clear()
            adapter?.notifyDataSetChanged()
        }
    }

    //Gets next page of popular movies call
    private fun fetchNextPage() {
        val disposable =
                TMDBService.queryPopularMovies(++popularMoviePageNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            ifViewAttached { view: PopularMoviesView ->
                                response.results?.map { popularMoviesList.add(it) }
                                view.setMovies(popularMoviesList)
                                view.hidePageLoad()
                                isRecyclerLoading = false
                            }
                        }, { throwable ->
                            ifViewAttached { view: PopularMoviesView ->
                                //todo: revisit erroring the whole page on this.  Just error bottom
                                view.showError(throwable, false)
                            }
                        })
        compositeDisposable.add(disposable)
    }

    fun onCardSelected(position: Int) {
        ifViewAttached { view: PopularMoviesView ->
            view.showDetail(Bundle().apply {
                putInt("MovieID", position)
            })
        }
    }

    fun fetchPosterArt(cardItemPosterView: ImageView, item: PopularMovieResultsItem) {
        Glide.with(cardItemPosterView).clear(cardItemPosterView)
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
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(releaseDate)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            outputFormat.format(date)
        } else {
            ""
        }
    }

    override fun destroy() {
        super.destroy()
        Timber.d("destroy called, disposables disposed of")
        compositeDisposable.dispose()
    }
}