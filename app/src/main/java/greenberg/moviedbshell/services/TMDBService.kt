package greenberg.moviedbshell.services

import greenberg.moviedbshell.models.moviedetailmodels.MovieDetailResponse
import greenberg.moviedbshell.models.peopledetailmodels.CombinedCreditsResponse
import greenberg.moviedbshell.models.peopledetailmodels.PersonDetailResponse
import greenberg.moviedbshell.models.popularmoviesmodels.PopularMovieResponse
import greenberg.moviedbshell.models.searchmodels.SearchResponse
import greenberg.moviedbshell.models.sharedmodels.CreditsResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvDetailResponse
import greenberg.moviedbshell.models.ui.PersonDetailCreditItem
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {
    @GET("movie/{id}")
    fun queryMovieDetail(@Path("id") id: Int): Single<MovieDetailResponse>

    @GET("movie/{id}/credits")
    fun queryMovieCredits(@Path("id") id: Int): Single<CreditsResponse>

    @GET("movie/popular")
    fun queryPopularMovies(@Query("page") page: Int): Single<PopularMovieResponse>

    @GET("search/multi")
    fun querySearchMulti(@Query("query") query: String, @Query("page") page: Int): Single<SearchResponse>

    @GET("tv/{id}")
    fun queryTvDetail(@Path("id") id: Int): Single<TvDetailResponse>

    @GET("tv/{id}/credits")
    fun queryTvCredits(@Path("id") id: Int): Single<CreditsResponse>

    @GET("person/{id}")
    fun queryPersonDetail(@Path("id") id: Int): Single<PersonDetailResponse>

    @GET("person/{id}/combined_credits")
    fun queryPersonCombinedCredits(@Path("id") id: Int): Single<CombinedCreditsResponse>
}