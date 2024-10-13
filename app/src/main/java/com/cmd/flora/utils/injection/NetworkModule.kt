package com.cmd.flora.utils.injection

import android.content.Context
import com.cmd.flora.BuildConfig
import com.cmd.flora.application.isInternetAvailable
import com.cmd.flora.data.network.base.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.time.Duration
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoAuthInterceptorClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideDispatcher(): Dispatcher {
        return Dispatcher().apply {
            this.maxRequests = 1
        }
    }

    @AuthInterceptorClient
    @Provides
    @Singleton
    fun provideAuthClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor,
        dispatcher: Dispatcher
    ): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(networkConnectionInterceptor)
            .connectTimeout(Duration.ofMinutes(1))
            .dispatcher(dispatcher)
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(loggingInterceptor)
        }

        return clientBuilder.build()
    }

    @NoAuthInterceptorClient
    @Provides
    @Singleton
    fun provideClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(networkConnectionInterceptor)
            .connectTimeout(Duration.ofMinutes(1))
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(loggingInterceptor)
        }

        return clientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    @Singleton
    fun provideNetworkConnectionInterceptor(@ApplicationContext context: Context): NetworkConnectionInterceptor {
        return NetworkConnectionInterceptor(context)
    }
}

class NetworkConnectionInterceptor(private val context: Context) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!context.isInternetAvailable()) {
            throw NoConnectivityException()
            // Throwing our custom exception 'NoConnectivityException'
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}

class NoConnectivityException : IOException() {
    override val message: String = "No Internet Connection"
}