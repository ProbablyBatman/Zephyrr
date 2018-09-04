package greenberg.moviedbshell.presenters

import android.content.Context
import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.R
import greenberg.moviedbshell.mappers.TvDetailMapper
import greenberg.moviedbshell.models.sharedmodels.CreditsResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvDetailResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvDetailResponseContainer
import greenberg.moviedbshell.models.ui.TvDetailItem
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.view.TvDetailView
import greenberg.moviedbshell.viewHolders.CastListAdapter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TvDetailPresenter
@Inject constructor(private val TMDBService: TMDBService,
                    private val context: Context,
                    private val mapper: TvDetailMapper) : MvpBasePresenter<TvDetailView>() {

    private var compositeDisposable = CompositeDisposable()
    private var castListAdapter: CastListAdapter? = null
    private var lastTvDetailItem: TvDetailItem? = null

    override fun attachView(view: TvDetailView) {
        super.attachView(view)
        Timber.d("attachView $this")
    }

    fun initView(tvShowId: Int, castListAdapter: CastListAdapter) {
        ifViewAttached { view: TvDetailView ->
            view.showLoading(tvShowId)
            this.castListAdapter = castListAdapter
        }
    }

    fun loadTvDetails(tvShowId: Int) {
        Timber.d("load tv details")
        //If there isn't an already existing item associated with this presenter.
        //Pages are mostly static, so data can sort of be retained like this. Potentially bad.
        if (lastTvDetailItem == null) {
            val disposable =
                    Single.zip(
                            TMDBService.queryTvDetail(tvShowId).subscribeOn(Schedulers.io()),
                            TMDBService.queryTvCredits(tvShowId).subscribeOn(Schedulers.io()),
                            BiFunction<TvDetailResponse, CreditsResponse, TvDetailResponseContainer> { tvDetail, tvCredits ->
                                TvDetailResponseContainer(tvDetail, tvCredits)
                            }
                    )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ tvDetailResponseItem ->
                                val tvDetailItem = mapper.mapToEntity(tvDetailResponseItem)
                                ifViewAttached { view: TvDetailView ->
                                    castListAdapter?.castMemberList?.addAll(tvDetailItem.castMembers)
                                    castListAdapter?.notifyDataSetChanged()
                                    castListAdapter?.onClickListener = { itemId: Int -> this.onCardSelected(itemId) }
                                    view.showTvDetails(tvDetailItem)
                                    lastTvDetailItem = tvDetailItem
                                }
                            }, { throwable ->
                                ifViewAttached { view: TvDetailView ->
                                    view.showError(throwable)
                                }
                            })
            compositeDisposable.add(disposable)
        } else {
            ifViewAttached { view: TvDetailView ->
                lastTvDetailItem?.let {
                    castListAdapter?.castMemberList?.addAll(it.castMembers.take(20))
                    castListAdapter?.notifyDataSetChanged()
                    castListAdapter?.onClickListener = { itemId: Int -> this.onCardSelected(itemId) }
                    view.showTvDetails(it)
                }
            }
        }
    }

    //TODO: probably make sure every date is like this?
    //there has to be a better way to do this
    fun processDate(releaseDate: String): String {
        return if (releaseDate.isNotBlank()) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(releaseDate)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            outputFormat.format(date)
        } else {
            ""
        }
    }

    fun processLastOrNextAirDateTitle(tvDetailItem: TvDetailItem): String? {
        return when {
            tvDetailItem.nextAirDate != null -> context.getString(R.string.next_air_date_title)
            tvDetailItem.lastAirDate != null -> context.getString(R.string.last_aired_title)
            else -> null
        }
    }

    fun processLastOrNextAirDate(tvDetailItem: TvDetailItem): String? {
        return when {
            tvDetailItem.nextAirDate != null -> processDate(tvDetailItem.nextAirDate)
            tvDetailItem.lastAirDate != null -> processDate(tvDetailItem.lastAirDate)
            else -> null
        }
    }

    fun processRatingInfo(voteAverage: Double, voteCount: Int): String {
        val formattedAverage: String = formatRatings(voteAverage)
        return context.getString(R.string.user_rating_substitution, formattedAverage, voteCount)
    }

    private fun formatRatings(voteAverage: Double?): String {
        val doubleFormat: NumberFormat = DecimalFormat("##.##")
        return doubleFormat.format(voteAverage)
    }

    fun processRuntime(runtime: List<Int>): String {
        val allRuntimes = runtime.joinToString(", ")
        return context.getString(R.string.runtime_substitution, allRuntimes)
    }

    fun processGenreTitle(genresListSize: Int): String = context.resources.getQuantityString(R.plurals.genres_title, genresListSize)

    fun processGenres(genres: List<String?>): String = genres.joinToString(", ")

    private fun onCardSelected(itemId: Int) {
        ifViewAttached { view: TvDetailView ->
            view.showDetail(Bundle().apply {
                putInt("PersonID", itemId)
            })
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