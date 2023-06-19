package greenberg.moviedbshell.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import greenberg.moviedbshell.BuildConfig
import greenberg.moviedbshell.R
import greenberg.moviedbshell.services.TMDBService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
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
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(context: Context): Cache {
        // 10mb cache size.  Probably won't need larger, probably won't need this.  Images are big sometimes, though.
        val cacheSize: Long = 10 * 1024 * 1024
        return Cache(context.cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(context: Context, cache: Cache): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .cache(cache)
            .apply {
                addInterceptor(loggingInterceptor)
                addInterceptor { chain ->
                    val original = chain.request()
                    val originalHttpUrl = original.url
                    val url = originalHttpUrl.newBuilder()
                        .addQueryParameter(context.getString(R.string.api_key), BuildConfig.TMDB_API_KEY)
                        .build()
                    val request = original.newBuilder().url(url).build()
                    val response = chain.proceed(request)
                    if (response.networkResponse == null) {
                        Timber.d("call hit the cache")
                    } else {
                        Timber.d("call hit the network")
                    }
                    response
                }
            }.build()
    }

    @Provides
    @Singleton
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
