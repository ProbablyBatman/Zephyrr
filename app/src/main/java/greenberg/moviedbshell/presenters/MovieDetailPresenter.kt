package greenberg.moviedbshell.presenters

import android.content.Context
import android.os.Bundle
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
import javax.inject.Inject

class MovieDetailPresenter
@Inject constructor(
    private val TMDBService: TMDBService,
    private val context: Context,
    private val mapper: MovieDetailMapper
) : MvpBasePresenter<MovieDetailView>() {

    private var compositeDisposable = CompositeDisposable()
    private var castListAdapter: CastListAdapter? = null
    private var lastMovieDetailItem: MovieDetailItem? = null

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

    // TODO: only grab 20 cast members for now.  Figure out limiting for that later
    // Note: There's an architectural issue at play here.  In terms of error loading, if one of these endpoints is no good
    // the whole page will fail to load.  I'm not sure what to do about that as it stands, however the first logical step
    // is separating these calls.  That would require nullable parameters in mapper, which is doable.  As it stands,
    // with the first round of error handling, just fail the whole page.  It is unlikely that a single endpoint from the
    // api will be down, instead the whole thing will just be down if that's the case.
    // This applies to all detail views.
    fun loadMovieDetails(movieId: Int) {
        ifViewAttached { view: MovieDetailView ->
            view.showLoading(movieId)
        }
        Timber.d("load movie details")
        if (lastMovieDetailItem == null) {
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
                                    castListAdapter?.castMemberList?.addAll(movieDetailItem.castMembers.take(20))
                                    castListAdapter?.notifyDataSetChanged()
                                    castListAdapter?.onClickListener = { itemId: Int -> this.onCardSelected(itemId) }
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
                lastMovieDetailItem?.let {
                    castListAdapter?.castMemberList?.addAll(it.castMembers.take(20))
                    castListAdapter?.notifyDataSetChanged()
                    castListAdapter?.onClickListener = { itemId: Int -> this.onCardSelected(itemId) }
                    view.showMovieDetails(it)
                }
            }
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

    fun processGenreTitle(genresListSize: Int): String = context.resources.getQuantityString(R.plurals.genres_title, genresListSize)

    fun processGenres(genres: List<String?>): String = genres.joinToString(", ")

    private fun onCardSelected(itemId: Int) {
        ifViewAttached { view: MovieDetailView ->
            view.showDetail(Bundle().apply {
                putInt("PersonID", itemId)
            })
        }
    }

    override fun destroy() {
        super.destroy()
        Timber.d("destroy called, disposables disposed of")
        compositeDisposable.dispose()
    }
}
