package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter

class AsteroidRepository(private val database: AsteroidDatabase) {

    /**
     * A list of asteroids (we get them from the database, but transform into
     * our domain model (sep of concerns logic)
     * This is the DatabaseAsteroid to domainModel ext func
     */
    val asteroids: LiveData<List<Asteroid>> =
            Transformations.map(database.asteroidDao.getAsteroids()) {
                it.asDomainModel()
            }

    val pictureOfDay: LiveData<PictureOfDay> = Transformations.map(
            database.asteroidDao.getPictureOfDay()){ it?.asDomainModel() }

    /**
     * Refresh the picture of the day in the offline cache
     */
    suspend fun refreshPictureOfDay(){
        withContext(Dispatchers.IO){
            try {

                Timber.d("Request new pictureOfDay...")
                val pictureOfDay = NasaApi.retrofitMoshiService.getPictureOfDay(
                        apiKey = Constants.API_KEY)
                // convert them to array of DatabaseAsteroids and insert all
                database.asteroidDao.insertPictureOfDay(pictureOfDay.asDatabaseModel())
            } catch (e: Exception) {
                Timber.e("Got exception when refreshing picture of day: $e")
            }
        }
    }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [asteroids]
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
                Timber.d("Request new asteroids w/ startDate $currentDate")
                val stringResponse = NasaApi.retrofitScalarService.getAsteroids(
                        apiKey = Constants.API_KEY, startDate = currentDate , endDate = null)
                Timber.d("The results from start $currentDate are $stringResponse")
                val networkAsteroids = parseAsteroidsJsonResult(JSONObject(stringResponse))
                // convert them to array of DatabaseAsteroids and insert all
                database.asteroidDao.insertAll(*networkAsteroids.asDatabaseModel())
            } catch (e: Exception) {
                Timber.e("Got exception when refreshing asteroids: $e")
            }
        }
    }

    suspend fun clearOldAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Clear old asteroids...")
                val today = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
                database.asteroidDao.clearOldAsteroids(today)
            } catch (e: Exception) {
                Timber.e("Got exception when clearing old asteroids: $e")
            }
        }
    }

    suspend fun clearOldPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Clear old picture of day...")
                val today = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
                database.asteroidDao.clearOldPictureOfDay(today)
            } catch (e: Exception) {
                Timber.e("Got exception when clearing old picture of day: $e")
            }
        }
    }
}


