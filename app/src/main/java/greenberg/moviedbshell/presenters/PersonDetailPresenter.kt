package greenberg.moviedbshell.presenters

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.mappers.PersonDetailMapper
import greenberg.moviedbshell.models.peopledetailmodels.CombinedCreditsResponse
import greenberg.moviedbshell.models.peopledetailmodels.PersonDetailResponse
import greenberg.moviedbshell.models.peopledetailmodels.PersonDetailResponseContainer
import greenberg.moviedbshell.models.ui.PersonDetailItem
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.view.PersonDetailView
import greenberg.moviedbshell.viewHolders.CreditsAdapter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class PersonDetailPresenter
@Inject constructor(
    private val TMDBService: TMDBService,
    private val mapper: PersonDetailMapper
) : MvpBasePresenter<PersonDetailView>() {

    private var compositeDisposable = CompositeDisposable()
    private var creditsAdapter: CreditsAdapter? = null
    private var lastPersonItem: PersonDetailItem? = null

    override fun attachView(view: PersonDetailView) {
        super.attachView(view)
        Timber.d("attachView")
    }

    fun initView(adapter: CreditsAdapter?) {
        ifViewAttached { view: PersonDetailView ->
            view.showLoading()
            this.creditsAdapter = adapter
        }
    }

    fun loadPersonDetails(personId: Int) {
        // If there isn't an already existing item associated with this presenter.
        // Pages are mostly static, so data can sort of be retained like this. Potentially bad.
        if (lastPersonItem == null) {
            val disposable =
                    Single.zip(
                            TMDBService.queryPersonDetail(personId).subscribeOn(Schedulers.io()),
                            TMDBService.queryPersonCombinedCredits(personId).subscribeOn(Schedulers.io()),
                            BiFunction<PersonDetailResponse, CombinedCreditsResponse, PersonDetailResponseContainer> { personDetail, personCredits ->
                                PersonDetailResponseContainer(personDetail, personCredits)
                            }
                    )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ personDetailResponseItem ->
                                val personDetailItem = mapper.mapToEntity(personDetailResponseItem)
                                ifViewAttached { view: PersonDetailView ->
                                    creditsAdapter?.creditsList?.addAll(personDetailItem.combinedCredits)
                                    creditsAdapter?.notifyDataSetChanged()
                                    view.showPersonDetails(personDetailItem)
                                    lastPersonItem = personDetailItem
                                }
                            }, { throwable ->
                                ifViewAttached { view: PersonDetailView ->
                                    view.showError(throwable)
                                }
                            })
            compositeDisposable.add(disposable)
        } else {
            ifViewAttached { view: PersonDetailView ->
                lastPersonItem?.let {
                    creditsAdapter?.creditsList?.addAll(it.combinedCredits)
                    creditsAdapter?.notifyDataSetChanged()
                    view.showPersonDetails(it)
                }
            }
        }
    }

    // TODO: probably make sure every date is like this?
    // there has to be a better way to do this
    fun processDate(date: String): String {
        return if (date.isNotBlank()) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = inputFormat.parse(date)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            outputFormat.format(formattedDate)
        } else {
            ""
        }
    }

    // TODO: probably make sure every date is like this?
    // there has to be a better way to do this
    fun processAge(birthday: String, deathday: String = ""): Int {
        var age: Int
        when {
            birthday.isNotEmpty() && deathday.isNotEmpty() -> {
                val splitBirthday = birthday.split("-").map { it.toInt() }
                val formattedBirthday = Calendar.getInstance().apply { set(splitBirthday[0], splitBirthday[1], splitBirthday[2]) }
                val splitDeathday = deathday.split("-").map { it.toInt() }
                val formattedDeathday = Calendar.getInstance().apply { set(splitDeathday[0], splitDeathday[1], splitDeathday[2]) }
                age = formattedDeathday.get(Calendar.YEAR) - formattedBirthday.get(Calendar.YEAR)
                if (formattedDeathday.get(Calendar.DAY_OF_YEAR) < formattedBirthday.get(Calendar.DAY_OF_YEAR)) {
                    age -= 1
                }
            }
            birthday.isNotEmpty() -> {
                val splitDate = birthday.split("-").map { it.toInt() }
                val formattedBirthday = Calendar.getInstance().apply { set(splitDate[0], splitDate[1], splitDate[2]) }
                val today = Calendar.getInstance()
                age = today.get(Calendar.YEAR) - formattedBirthday.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < formattedBirthday.get(Calendar.DAY_OF_YEAR)) {
                    age -= 1
                }
            }
            else -> {
                age = -1
            }
        }
        return age
    }

    fun onCardSelected(itemId: Int, mediaType: String) {
        ifViewAttached { view: PersonDetailView ->
            view.showDetail(Bundle().apply {
                if (mediaType == SearchPresenter.MEDIA_TYPE_MOVIE) putInt("MovieID", itemId)
                else if (mediaType == SearchPresenter.MEDIA_TYPE_TV) putInt("TvDetailID", itemId)
            }, mediaType)
        }
    }

    override fun detachView() {
        Timber.d("detachView")
        super.detachView()
    }

    override fun destroy() {
        Timber.d("destroy called, disposables disposed of")
        compositeDisposable.dispose()
        super.destroy()
    }
}