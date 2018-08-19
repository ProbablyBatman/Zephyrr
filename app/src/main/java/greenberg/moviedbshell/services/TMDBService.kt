package greenberg.moviedbshell.services

import greenberg.moviedbshell.models.MovieDetailModels.MovieDetailResponse
import greenberg.moviedbshell.models.PopularMoviesModels.PopularMovieResponse
import greenberg.moviedbshell.models.SearchModels.SearchResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {
    @GET("movie/{id}")
    fun queryMovies(@Path("id") id: Int): Single<MovieDetailResponse>

    @GET("movie/popular")
    fun queryPopularMovies(@Query("page") page: Int): Single<PopularMovieResponse>

    @GET("search/multi")
    fun querySearchMulti(@Query("query") query: String, @Query("page") page: Int): Single<SearchResponse>
}