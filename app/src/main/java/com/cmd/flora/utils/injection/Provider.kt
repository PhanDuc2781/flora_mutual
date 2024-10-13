package com.cmd.flora.utils.injection

import com.cmd.flora.BuildConfig
import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.base.ApiService
import com.cmd.flora.data.network.base.AuthApiService
import com.cmd.flora.data.network.base.AuthInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Provider {
    @Singleton
    @Provides
    fun provideAuthInterceptor(
        gson: Gson,
        service: ApiService
    ): AuthInterceptor {
        return AuthInterceptor(service, gson)
    }

    @Singleton
    @Provides
    fun provideApiRequest(
        authService: AuthApiService,
        noAuthService: ApiService,
        gson: Gson
    ): APIRequest {
        return APIRequest(authService, noAuthService, gson)
    }

    @Singleton
    @Provides
    fun provideAuthRetrofit(
        @AuthInterceptorClient okHttpClient: OkHttpClient,
        retrofitBuilder: Retrofit.Builder
    ): AuthApiService {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(AuthApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        @NoAuthInterceptorClient okHttpClient: OkHttpClient,
        retrofitBuilder: Retrofit.Builder
    ): ApiService {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(gsonConverterFactory: GsonConverterFactory): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(gsonConverterFactory)

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().generateNonExecutableJson().create()
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }
}