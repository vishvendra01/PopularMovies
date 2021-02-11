package com.popmovies.rest

import android.util.Log
import com.popmovies.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MoviesApiClient {
    companion object {
        private const val TAG = "MoviesApiClient"
    }

    fun getMoviesApi(): MoviesApi {
        return Retrofit.Builder()
            .baseUrl(MoviesApi.BASE_URL)
            .client(buildHTTPClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoviesApi::class.java)
    }

    private fun buildHTTPClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(10, TimeUnit.SECONDS)
        httpClient.readTimeout(10, TimeUnit.SECONDS)
        httpClient.writeTimeout(120, TimeUnit.SECONDS)
        httpClient.addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "log: $message")
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY))
        return httpClient.build()
    }
}