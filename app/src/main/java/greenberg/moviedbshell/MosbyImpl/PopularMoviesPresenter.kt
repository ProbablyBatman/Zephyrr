package greenberg.moviedbshell.MosbyImpl

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.RetrofitHelpers.RetrofitHelper
import greenberg.moviedbshell.RetrofitHelpers.TMDBService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PopularMoviesPresenter : MvpBasePresenter<PopularMoviesView>() {

    //TODO: revisit this stupid thing
    private var TMDBService: TMDBService = RetrofitHelper().getTMDBService()

    fun loadPopularMovies(pullToRefresh: Boolean) {
        TMDBService.queryPopularMovies()
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

    fun showError(throwable: Throwable, pullToRefresh: Boolean) {

    }

    override fun detachView() {
        super.detachView()
        //TODO: cancel anything going on here, though press x to doubt
    }
}