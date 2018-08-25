package greenberg.moviedbshell.services

import greenberg.moviedbshell.models.moviedetailmodels.MovieDetailResponse
import greenberg.moviedbshell.models.popularmoviesmodels.PopularMovieResponse
import greenberg.moviedbshell.models.searchmodels.SearchResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvDetailCreditsResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvDetailResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {
    @GET("movie/{id}")
    fun queryMovieDetail(@Path("id") id: Int): Single<MovieDetailResponse>

    @GET("movie/popular")
    fun queryPopularMovies(@Query("page") page: Int): Single<PopularMovieResponse>

    @GET("search/multi")
    fun querySearchMulti(@Query("query") query: String, @Query("page") page: Int): Single<SearchResponse>

    @GET("tv/{id}")
    fun queryTvDetail(@Path("id") id: Int): Single<TvDetailResponse>

    @GET("tv/{id}/credits")
    fun queryTvCredits(@Path("id") id: Int): Single<TvDetailCreditsResponse>
}