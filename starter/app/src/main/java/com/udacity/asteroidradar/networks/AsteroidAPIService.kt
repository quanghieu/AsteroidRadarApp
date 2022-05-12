package com.udacity.asteroidradar.networks

import com.udacity.asteroidradar.Constants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(1, TimeUnit.MINUTES)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()



interface AsteroidAPIService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(@Query("start_date") start_date : String, @Query("api_key") api_key : String) : String

    @GET("planetary/apod")
    suspend fun getImageOfTheDay(@Query("api_key") api_key: String) : String
}

object AsteroidApi {
    val retrofitAPIService : AsteroidAPIService by lazy {
        retrofit.create(AsteroidAPIService::class.java)
    }
}