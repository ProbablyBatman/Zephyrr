package greenberg.moviedbshell.RetrofitHelpers

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

//TODO: I'm not entirely sure of the architecture or intention of this as a class, I suppose it's purpose
//is to kind of keep like a singleton pattern around.
class RetrofitHelper {

    fun getTMDBService(): TMDBService {
        val retrofit = createRetrofit()
        return retrofit.create(TMDBService::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
                val original = chain.request()
                val originalHttpUrl = original.url()
                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", "4abfb5ed425359a75aa7af8228e5c191")
                    .build()
                val requestBuilder = original.newBuilder()
                        .url(url)
                val request = requestBuilder.build()
                 chain.proceed(request)
        }
        return httpClient.build()
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()
    }
}