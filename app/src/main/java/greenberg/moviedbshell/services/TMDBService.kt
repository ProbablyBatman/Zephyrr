package greenberg.moviedbshell.services

import greenberg.moviedbshell.models.imagegallerymodels.ImageGalleryResponse
import greenberg.moviedbshell.models.moviedetailmodels.MovieDetailResponse
import greenberg.moviedbshell.models.movielistmodels.MovieListResponse
import greenberg.moviedbshell.models.peopledetailmodels.CombinedCreditsResponse
import greenberg.moviedbshell.models.peopledetailmodels.PersonDetailResponse
import greenberg.moviedbshell.models.searchmodels.SearchResponse
import greenberg.moviedbshell.models.sharedmodels.CreditsResponse
import greenberg.moviedbshell.models.tvdetailmodels.AggregateCreditsResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvShowResponse
import greenberg.moviedbshell.models.tvlistmodels.TvListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {
    @GET("movie/{id}")
    suspend fun queryMovieDetail(@Path("id") id: Int): MovieDetailResponse

    @GET("movie/{id}/credits")
    suspend fun queryMovieCredits(@Path("id") id: Int): CreditsResponse

    @GET("movie/{id}/images")
    suspend fun queryMovieImages(@Path("id") id: Int): ImageGalleryResponse

    @GET("movie/popular")
    suspend fun queryPopularMovies(@Query("page") page: Int): MovieListResponse

    @GET("search/multi")
    suspend fun querySearchMulti(@Query("query") query: String, @Query("page") page: Int): SearchResponse

    @GET("tv/{id}")
    suspend fun queryTvDetail(@Path("id") id: Int): TvShowResponse

    @GET("tv/{id}/credits")
    suspend fun queryTvCredits(@Path("id") id: Int): CreditsResponse

    @GET("tv/{id}/aggregate_credits")
    suspend fun queryTvAggregateCredits(@Path("id") id: Int): AggregateCreditsResponse

    @GET("tv/{id}/images")
    suspend fun queryTvImages(@Path("id") id: Int): ImageGalleryResponse

    @GET("person/{id}")
    suspend fun queryPersonDetail(@Path("id") id: Int): PersonDetailResponse

    @GET("person/{id}/combined_credits")
    suspend fun queryPersonCombinedCredits(@Path("id") id: Int): CombinedCreditsResponse

    @GET("search/person")
    suspend fun querySearchPerson(@Query("query") query: String, @Query("page") page: Int): SearchResponse

    @GET("discover/movie")
    suspend fun queryMovieDiscover(@Query("page") page: Int, @Query("with_cast", encoded = true) castList: String): SearchResponse

    @GET("discover/tv")
    suspend fun queryTvDiscover(@Query("page") page: Int, @Query("with_cast", encoded = true) castList: String): SearchResponse

    /**
     * This is implemented for now only against English releases. It is probably a bit complex to go global.
     */
    @GET("movie/now_playing")
    suspend fun queryRecentlyReleased(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
    ): MovieListResponse

    @GET("movie/upcoming")
    suspend fun querySoonTM(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "US",
    ): MovieListResponse

    @GET("tv/popular")
    suspend fun queryPopularTv(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "US",
    ): TvListResponse

    @GET("tv/top_rated")
    suspend fun queryTopRatedTv(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "US",
    ): TvListResponse
}
