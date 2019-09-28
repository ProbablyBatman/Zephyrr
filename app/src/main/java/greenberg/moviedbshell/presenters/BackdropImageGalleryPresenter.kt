package greenberg.moviedbshell.presenters

import android.content.Context
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.ImagePagerAdapter
import greenberg.moviedbshell.mappers.BackdropImageMapper
import greenberg.moviedbshell.models.imagegallerymodels.BackdropsItem
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.view.BackdropImageGalleryView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class BackdropImageGalleryPresenter
@Inject constructor(
        private val TMDBService: TMDBService,
        private val context: Context,
        private val mapper: BackdropImageMapper
) : MvpBasePresenter<BackdropImageGalleryView>() {

    private var compositeDisposable = CompositeDisposable()
    private lateinit var mostRecentBackdropsItems: List<BackdropsItem>

    override fun attachView(view: BackdropImageGalleryView) {
        super.attachView(view)
        Timber.d("attachView")
    }

    fun initView() {
        ifViewAttached { view: BackdropImageGalleryView ->
            view.showLoading()
        }
    }

    fun loadImages(entityId: Int) {
        ifViewAttached { view: BackdropImageGalleryView ->
            view.showLoading()
        }
        Timber.d("load backdrop images")
        val disposable = TMDBService.queryMovieImages(entityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ifViewAttached { view: BackdropImageGalleryView ->
                        val backdropsItem = mapper.mapToEntity(response)
                                .filter { it.filePath != null && it.filePath.isNotEmpty() }
                        view.showImages(backdropsItem)
                        mostRecentBackdropsItems = backdropsItem
                    }
                }, { throwable ->
                    ifViewAttached { view: BackdropImageGalleryView ->
                        view.showError(throwable)
                    }
                })
        compositeDisposable.add(disposable)
    }

    // This function is probably bad practice, but is the most practical way around getting the current
    // image's url
    fun getCurrentImageLink(currentPagerPosition: Int): String {
        return context.getString(R.string.poster_url_substitution, mostRecentBackdropsItems[currentPagerPosition].filePath)
    }

    override fun detachView() {
        Timber.d("Detach view")
        super.detachView()
    }

    override fun destroy() {
        Timber.d("destroy called")
        compositeDisposable.dispose()
        super.destroy()
    }
}