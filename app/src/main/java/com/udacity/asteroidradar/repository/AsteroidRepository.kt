package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


fun getFormattedDate(days: Int=0): String{
    val calendar = Calendar.getInstance()
    if(days > 0){
        calendar.add(Calendar.DAY_OF_YEAR, days)
    }
    val currentTime = calendar.time
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(currentTime)
}


class AsteroidRepository(private val database: AsteroidDatabase) {


    /**
     * A list of asteroids (we get them from the database, but transform into
     * our domain model (sep of concerns logic)
     * This is the DatabaseAsteroid to domainModel ext func
     */
    val asteroids: LiveData<List<Asteroid>> =
            Transformations.map(database.asteroidDao.getAsteroids()){ it.asDomainModel()}
    val todayAsteroids: LiveData<List<Asteroid>> =
            Transformations.map(database.asteroidDao.getTodayAsteroids()){ it.asDomainModel()}
    val weeklyAsteroids: LiveData<List<Asteroid>> =
            Transformations.map(database.asteroidDao.getWeeklyAsteroids()){ it.asDomainModel()}

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
//                runBlocking {
//                    TimeUnit.SECONDS.sleep(10L)
//                }
                val today = getFormattedDate()
                Timber.d("Request new asteroids w/ startDate $today")
                val stringResponse = NasaApi.retrofitScalarService.getAsteroids(
                        apiKey = Constants.API_KEY, startDate = today , endDate = null)
                Timber.d("The results from start $today are $stringResponse")
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
                val today = getFormattedDate()
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
                val today = getFormattedDate()
                database.asteroidDao.clearOldPictureOfDay(today)
            } catch (e: Exception) {
                Timber.e("Got exception when clearing old picture of day: $e")
            }
        }
    }
}


