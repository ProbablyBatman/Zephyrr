package greenberg.moviedbshell.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import greenberg.moviedbshell.BuildConfig
import greenberg.moviedbshell.R
import greenberg.moviedbshell.services.TMDBService
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ServicesModule {

    @Provides
    @Singleton
    fun provideTMDBService(retrofit: Retrofit): TMDBService {
        return retrofit.create(TMDBService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(context: Context, httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(context.getString(R.string.tmdb_url))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(context: Context): Cache {
        //10mb cache size.  Probably won't need larger, probably won't need this.  Images are big sometimes, though.
        val cacheSize: Long = 10 * 1024 * 1024
        return Cache(context.cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(context: Context, cache: Cache): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
                .cache(cache)
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(loggingInterceptor)
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()
            val url = originalHttpUrl.newBuilder()
                    .addQueryParameter(context.getString(R.string.api_key), BuildConfig.TMDB_API_KEY)
                    .build()
            val request = original.newBuilder().url(url).build()
            chain.proceed(request)
        }
        return httpClient.build()
    }
}