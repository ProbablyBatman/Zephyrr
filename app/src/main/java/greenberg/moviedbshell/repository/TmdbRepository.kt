package greenberg.moviedbshell.repository

import greenberg.moviedbshell.base.ZephyrrResponse
import greenberg.moviedbshell.mappers.MovieDetailMapper
import greenberg.moviedbshell.mappers.PersonDetailMapper
import greenberg.moviedbshell.mappers.TvDetailMapper
import greenberg.moviedbshell.models.container.MovieDetailResponseContainer
import greenberg.moviedbshell.models.container.PersonDetailResponseContainer
import greenberg.moviedbshell.models.container.TvDetailResponseContainer
import greenberg.moviedbshell.models.imagegallerymodels.ImageGalleryResponse
import greenberg.moviedbshell.models.sharedmodels.CreditsResponse
import greenberg.moviedbshell.models.tvdetailmodels.AggregateCreditsResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvShowResponse
import greenberg.moviedbshell.models.ui.MovieDetailItem
import greenberg.moviedbshell.models.ui.PersonDetailItem
import greenberg.moviedbshell.models.ui.TvDetailItem
import greenberg.moviedbshell.services.TMDBService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class TmdbRepository
@Inject constructor(
    private val tmdbService: TMDBService,
    private val movieDetailMapper: MovieDetailMapper,
    private val tvDetailMapper: TvDetailMapper,
    private val personDetailMapper: PersonDetailMapper,
) {

    // Propagates errors up through the .execute on the suspend block
    suspend fun fetchMovieDetail(id: Int): MovieDetailItem {
        val details = tmdbService.queryMovieDetail(id)
        val credits = tmdbService.queryMovieCredits(id)
        val images = tmdbService.queryMovieImages(id)
        return movieDetailMapper.mapToEntity(
            MovieDetailResponseContainer(
                details,
                credits,
                images
            )
        )
    }

    suspend fun fetchMovieDetail(scope: CoroutineScope, id: Int): MovieDetailItem {
        val details = scope.async { tmdbService.queryMovieDetail(id) }
        val credits = scope.async { tmdbService.queryMovieCredits(id) }
        val images = scope.async { tmdbService.queryMovieImages(id) }
        return movieDetailMapper.mapToEntity(
            MovieDetailResponseContainer(
                details.await(),
                credits.await(),
                images.await()
            )
        )
    }

    suspend fun fetchTvDetail(id: Int): TvDetailItem {
        val details = tmdbService.queryTvDetail(id)
        val credits = tmdbService.queryTvCredits(id)
        val images = tmdbService.queryTvImages(id)
        val aggregateCredits = tmdbService.queryTvAggregateCredits(id)
        return tvDetailMapper.mapToEntity(
            TvDetailResponseContainer(
                details,
                credits,
                aggregateCredits,
                images
            )
        )
    }

    suspend fun fetchTvDetail(scope: CoroutineScope, id: Int): TvDetailItem {
        val details = scope.async { tmdbService.queryTvDetail(id) }
        val credits = scope.async { tmdbService.queryTvCredits(id) }
        val images = scope.async { tmdbService.queryTvImages(id) }
        val aggregateCredits = scope.async { tmdbService.queryTvAggregateCredits(id) }
        return tvDetailMapper.mapToEntity(
            TvDetailResponseContainer(
                details.await(),
                credits.await(),
                aggregateCredits.await(),
                images.await()
            )
        )
    }

    suspend fun fetchTvDetail2(id: Int) =
        zipper(
            { tmdbService.queryTvDetail(id) },
            { tmdbService.queryTvCredits(id) },
            { tmdbService.queryTvAggregateCredits(id) },
            { tmdbService.queryTvImages(id) }
        ) {
            tvDetailMapper.mapToEntity(
                TvDetailResponseContainer(
                    it.component1() as TvShowResponse,
                    it.component2() as CreditsResponse,
                    it.component3() as AggregateCreditsResponse,
                    it.component4() as ImageGalleryResponse
                )
            )
        }

    suspend fun <T> zipper(vararg func: suspend () -> Any, mapper: (List<Any>) -> T): T {
        return coroutineScope {
            val deferreds = mutableListOf<Deferred<Any>>()
            func.forEach { deferreds.add(async { it() }) }
            mapper.invoke(deferreds.awaitAll())
        }
    }

    suspend fun fetchPersonDetail(id: Int): PersonDetailItem {
        val details = tmdbService.queryPersonDetail(id)
        val credits = tmdbService.queryPersonCombinedCredits(id)
        return personDetailMapper.mapToEntity(
            PersonDetailResponseContainer(
                details,
                credits
            )
        )
    }

    suspend fun fetchRecentlyReleased(page: Int) = safeFetch { tmdbService.queryRecentlyReleased(page) }

    suspend fun fetchPopularMovies(page: Int) = safeFetch { tmdbService.queryPopularMovies(page) }

    suspend fun fetchSoonTM(page: Int) = safeFetch { tmdbService.querySoonTM(page) }

    suspend fun fetchPopularTv(page: Int) = safeFetch { tmdbService.queryPopularTv(page) }

    suspend fun fetchTopRatedTv(page: Int) = safeFetch { tmdbService.queryTopRatedTv(page) }

    suspend fun fetchMovieImages(id: Int) = tmdbService.queryMovieImages(id)

    suspend fun fetchTvImages(id: Int) = tmdbService.queryTvImages(id)

    suspend fun fetchSearchMulti(query: String, page: Int) = tmdbService.querySearchMulti(query, page)

    private suspend fun <T> safeFetch(call: suspend () -> T): ZephyrrResponse<T> {
        return try {
            ZephyrrResponse.Success(call.invoke())
        } catch (throwable: Throwable) {
            ZephyrrResponse.Failure(throwable)
        }
    }
}
