package greenberg.moviedbshell.presenters

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.view.AboutView
import timber.log.Timber
import javax.inject.Inject

class AboutPresenter
@Inject constructor() : MvpBasePresenter<AboutView>() {
    override fun attachView(view: AboutView) {
        super.attachView(view)
        Timber.d("attachView")
    }

    fun initView() {
        ifViewAttached { view: AboutView ->
            view.show()
        }
    }
}