package greenberg.moviedbshell.presenters

import android.content.Context
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.adapters.ImagePagerAdapter
import greenberg.moviedbshell.mappers.BackdropImageMapper
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
                    }
                }, { throwable ->
                    ifViewAttached { view: BackdropImageGalleryView ->
                        view.showError(throwable)
                    }
                })
        compositeDisposable.add(disposable)
    }

    fun fetchImageLinks(entityId: Int, adapter: ImagePagerAdapter) {
        val disposable = TMDBService.queryMovieImages(entityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ifViewAttached { view: BackdropImageGalleryView ->
                        val backdropsItem = mapper.mapToEntity(response)
                                .filter { it.filePath != null && it.filePath.isNotEmpty() }
                        view.showImages(backdropsItem)

                    }
                }, { throwable ->
                    ifViewAttached { view: BackdropImageGalleryView ->
                        view.showError(throwable)
                    }
                })
        compositeDisposable.add(disposable)
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