package greenberg.moviedbshell.presenters

import android.content.Context
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.R
import greenberg.moviedbshell.mappers.TvDetailMapper
import greenberg.moviedbshell.models.sharedmodels.CreditsResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvDetailResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvDetailResponseItem
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
    private lateinit var lastTvDetailItem: TvDetailItem

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
        //TODO: look up a proper check for adding this disposable.
        //Problem is I have no idea how to add it and keep track of it in a non disgusting way
        //I think that the movie detail also has this problem
        if (!compositeDisposable.isDisposed) {
            val disposable =
                    Single.zip(
                            TMDBService.queryTvDetail(tvShowId).subscribeOn(Schedulers.io()),
                            TMDBService.queryTvCredits(tvShowId).subscribeOn(Schedulers.io()),
                            BiFunction<TvDetailResponse, CreditsResponse, TvDetailResponseItem> { tvDetail, tvCredits ->
                                TvDetailResponseItem(tvDetail, tvCredits)
                            }
                    )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ tvDetailResponseItem ->
                                val tvDetailItem = mapper.mapToEntity(tvDetailResponseItem)
                                ifViewAttached { view: TvDetailView ->
                                    castListAdapter?.castList?.addAll(tvDetailItem.castMembers)
                                    castListAdapter?.notifyDataSetChanged()
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
                castListAdapter?.castList?.addAll(lastTvDetailItem.castMembers)
                castListAdapter?.notifyDataSetChanged()
                view.showTvDetails(lastTvDetailItem)
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
            tvDetailItem.nextAirDate != null -> context.getString(R.string.next_air_date)
            tvDetailItem.lastAirDate != null -> context.getString(R.string.last_aired)
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

    fun processGenreTitle(genresListSize: Int): String = context.resources.getQuantityString(R.plurals.genres_bold, genresListSize)

    fun processGenres(genres: List<String?>): String = genres.joinToString(", ")

    override fun detachView() {
        super.detachView()
        Timber.d("detachView")
    }

    override fun destroy() {
        super.destroy()
        Timber.d("destroy called, disposables disposed of")
        compositeDisposable.dispose()
    }
}