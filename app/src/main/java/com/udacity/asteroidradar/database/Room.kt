package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AsteroidDao {
    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate = date('now') order by closeApproachDate asc")
    fun getTodayAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate BETWEEN date('now') AND date('now', '+7 day') order by closeApproachDate asc")
    fun getWeeklyAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid  order by closeApproachDate asc")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    // Picture of the day
    @Query("select * from databasepictureofday order by created_at desc limit 1")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(pictureOfDay: DatabasePictureOfDay)

    @Query("delete from databaseasteroid where closeApproachDate < :today")
    fun clearOldAsteroids(today: String)

    @Query("delete from databasepictureofday where created_at < :today")
    fun clearOldPictureOfDay(today: String)

}

@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 2)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
//    synchronized so it is thread safe and we only ever get one db instance
    // added .fallbackToDestructiveMigration() to avoid migrations when upgrading db version
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AsteroidDatabase::class.java,
                    "asteroids").fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}