package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.domain.Asteroid
import retrofit2.Retrofit
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.domain.PictureOfDay
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Use the Retrofit builder to build a retrofit object using a scalar converter
 * s this response cannot be parsed directly with Moshi, we are providing a method to
 * parse the data “manually” for you, it’s called parseAsteroidsJsonResult inside NetworkUtils class
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()


/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 *
 * Download Picture of Day JSON, parse it using Moshi
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit_moshi = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()


/**
 * A public interface that exposes the [getProperties] method
 */
interface NasaApiService {
    /**
     * Returns a Coroutine [List] of [Asteroid] which can be fetched with await() if in a
     * Coroutine scope. The @GET annotation indicates that the "neo/rest/v1/feed" endpoint will be
     * requested with the GET HTTP method
     */
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(@Query("start_date") startDate: String?,
                        @Query("end_date") endDate: String?,
                        @Query("api_key") apiKey: String): String

    @GET("planetary/apod")
    suspend fun getPictureOfDay(@Query("api_key") apiKey: String): NetworkPictureOfDay
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit services
 */
object  NasaApi {
    val retrofitScalarService : NasaApiService by lazy {
        retrofit.create(NasaApiService::class.java)
    }
    val retrofitMoshiService : NasaApiService by lazy {
        retrofit_moshi.create(NasaApiService::class.java)
    }
}