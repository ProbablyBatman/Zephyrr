package greenberg.moviedbshell.services

import greenberg.moviedbshell.models.imagegallerymodels.ImageGalleryResponse
import greenberg.moviedbshell.models.moviedetailmodels.MovieDetailResponse
import greenberg.moviedbshell.models.peopledetailmodels.CombinedCreditsResponse
import greenberg.moviedbshell.models.peopledetailmodels.PersonDetailResponse
import greenberg.moviedbshell.models.popularmoviesmodels.PopularMovieResponse
import greenberg.moviedbshell.models.recentlyreleasedmodels.RecentlyReleasedResponse
import greenberg.moviedbshell.models.searchmodels.SearchResponse
import greenberg.moviedbshell.models.sharedmodels.CreditsResponse
import greenberg.moviedbshell.models.soontmmodels.SoonTMResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvDetailResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {
    @GET("movie/{id}")
    fun queryMovieDetail(@Path("id") id: Int): Single<MovieDetailResponse>

    @GET("movie/{id}/credits")
    fun queryMovieCredits(@Path("id") id: Int): Single<CreditsResponse>

    @GET("movie/{id}/images")
    fun queryMovieImages(@Path("id") id: Int): Single<ImageGalleryResponse>

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

    /**
     * This is implemented for now only against English releases. It is probably a bit complex to go global.
     */
    @GET("movie/now_playing")
    fun queryRecentlyReleased(
            @Query("page") page: Int,
            @Query("language") language: String = "en-US"
    ): Single<RecentlyReleasedResponse>

    @GET("movie/upcoming")
    fun querySoonTM(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "US"
    ): Single<SoonTMResponse>
}