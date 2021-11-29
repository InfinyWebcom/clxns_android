package com.clxns.app.di

import android.content.Context
import com.clxns.app.BuildConfig
import com.clxns.app.data.api.ApiService
import com.clxns.app.data.api.helper.NetworkConnectionInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_DEV_URL


    @Provides
    @Singleton
    fun provideMoshi() : Moshi = Moshi.Builder().build()


    @Provides
    @Singleton
    fun provideConnectionInterceptor(
        @ApplicationContext app : Context
    ) = NetworkConnectionInterceptor(app)

    @Provides
    @Singleton
    fun provideHTTPClient(
        networkConnectionInterceptor : NetworkConnectionInterceptor
    ) = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(networkConnectionInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    } else OkHttpClient
        .Builder()
        .readTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(networkConnectionInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okkHttpclient : OkHttpClient,
        moshi : Moshi,
        BASE_URL : String
    ) : Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .client(okkHttpclient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit : Retrofit) : ApiService =
        retrofit.create(ApiService::class.java)
}