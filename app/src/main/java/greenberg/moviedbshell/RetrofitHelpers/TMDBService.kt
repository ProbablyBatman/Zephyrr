package greenberg.moviedbshell.RetrofitHelpers

import greenberg.moviedbshell.Models.MovieDetailModels.MovieDetailResponse
import greenberg.moviedbshell.Models.PopularMoviesModels.PopularMovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {
    @GET("movie/{id}")
    fun queryMovies(@Path("id") id: Int): Single<MovieDetailResponse>

    @GET("movie/popular")
    fun queryPopularMovies(@Query("page") page: Int): Single<PopularMovieResponse>
    //fun queryMovies(@Path("id") id: Int): Call<MovieDetailResponse>
}