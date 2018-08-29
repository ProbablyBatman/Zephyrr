package greenberg.moviedbshell.presenters

import android.content.Context
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import greenberg.moviedbshell.R
import greenberg.moviedbshell.mappers.MovieDetailMapper
import greenberg.moviedbshell.models.moviedetailmodels.MovieDetailResponse
import greenberg.moviedbshell.models.sharedmodels.CreditsResponse
import greenberg.moviedbshell.models.ui.MovieDetailItem
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.view.MovieDetailView
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

class MovieDetailPresenter
@Inject constructor(private val TMDBService: TMDBService,
                    private val context: Context,
                    private val mapper: MovieDetailMapper) : MvpBasePresenter<MovieDetailView>() {

    private var compositeDisposable = CompositeDisposable()
    private var castListAdapter: CastListAdapter? = null
    private lateinit var lastMovieDetailItem: MovieDetailItem

    override fun attachView(view: MovieDetailView) {
        super.attachView(view)
        Timber.d("attachView")
    }

    fun initView(movieId: Int, castListAdapter: CastListAdapter) {
        ifViewAttached { view: MovieDetailView ->
            view.showLoading(movieId)
            this.castListAdapter = castListAdapter
        }
    }

    //TODO: only grab 20 cast members for now.  Figre out limiting for that later
    fun loadMovieDetails(movieId: Int) {
        Timber.d("load movie details")
        if (!compositeDisposable.isDisposed) {
            val disposable =
                    Single.zip(
                            TMDBService.queryMovieDetail(movieId).subscribeOn(Schedulers.io()),
                            TMDBService.queryMovieCredits(movieId).subscribeOn(Schedulers.io()),
                            BiFunction<MovieDetailResponse, CreditsResponse, MovieDetailItem> { movieDetail, movieCredits ->
                                mapper.mapToEntity(Pair(movieDetail, movieCredits))
                            }
                    )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ movieDetailItem ->
                                ifViewAttached { view: MovieDetailView ->
                                    castListAdapter?.castList?.addAll(movieDetailItem.castMembers.take(20))
                                    castListAdapter?.notifyDataSetChanged()
                                    view.showMovieDetails(movieDetailItem)
                                    lastMovieDetailItem = movieDetailItem
                                }

                            }, { throwable ->
                                ifViewAttached { view: MovieDetailView ->
                                    view.showError(throwable)
                                }
                            })
            compositeDisposable.add(disposable)
        } else {
            ifViewAttached { view: MovieDetailView ->
                castListAdapter?.castList?.addAll(lastMovieDetailItem.castMembers.take(20))
                castListAdapter?.notifyDataSetChanged()
                view.showMovieDetails(lastMovieDetailItem)
            }
        }
    }

    //TODO: probably make sure every date is like this?
    //there has to be a better way to do this
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

    fun processRatingInfo(voteAverage: Double, voteCount: Int): String {
        val formattedAverage: String = formatRatings(voteAverage)
        return context.getString(R.string.user_rating_substitution, formattedAverage, voteCount)
    }

    private fun formatRatings(voteAverage: Double?): String {
        val doubleFormat: NumberFormat = DecimalFormat("##.##")
        return doubleFormat.format(voteAverage)
    }

    fun processRuntime(runtime: Int): String = context.getString(R.string.runtime_substitution, runtime.toString())

    fun processGenreTitle(genresListSize: Int): String = context.resources.getQuantityString(R.plurals.genres_bold, genresListSize)

    fun processGenres(genres: List<String?>): String = genres.joinToString(", ")

    override fun destroy() {
        super.destroy()
        Timber.d("destroy called, disposables disposed of")
        compositeDisposable.dispose()
    }
}
