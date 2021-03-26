package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel : ViewModel() {

    // Internally, we use a MutableLiveData, because we will be updating the List of Asteroids
    // with new values
    private val _asteroids = MutableLiveData<List<Asteroid>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids


    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay


    /**
     * Call getAsteroids() on init so we can display immediately.
     */
    init {
        getAsteroids()
        getPictureOfDay()
    }

    /**
     * Gets Asteroids from the Nasa API Retrofit service and
     * updates the [Asteroid] [List]. The Retrofit service
     * returns a coroutine Deferred, which we await to get the result of the transaction.
     */
    private fun getAsteroids() {
        viewModelScope.launch {
            try {
                _asteroids.value = parseAsteroidsJsonResult(
                    JSONObject(NasaApi.retrofitScalarService.getFeed(apiKey = Constants.API_KEY,
                    startDate = null , endDate = null)))
            } catch (e: Exception) {
                _asteroids.value = ArrayList()
            }
        }
    }

    /**
     * Get the picture of the the day to display on the main view
     */
    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = NasaApi.retrofitMoshiService.getPictureOfDay(
                    apiKey = Constants.API_KEY)
            } catch (e: Exception) {
                _pictureOfDay.value = null
            }
        }
    }

}