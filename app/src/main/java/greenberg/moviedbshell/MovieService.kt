package greenberg.moviedbshell

import greenberg.moviedbshell.Models.MovieResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieService {
    @GET("movie/{id}")
    fun queryMovies(@Path("id") id: Int): Single<MovieResponse>
    //fun queryMovies(@Path("id") id: Int): Call<MovieResponse>
}