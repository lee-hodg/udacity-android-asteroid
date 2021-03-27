package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.domain.PictureOfDay

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid order by closeApproachDate asc")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    // Picture of the day
    @Query("select * from databasepictureofday order by created_at desc limit 1")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(pictureOfDay: DatabasePictureOfDay)

//    fun insertPictureOfDayWithTimestamp(pictureOfDay: DatabasePictureOfDay) {
//        insertPictureOfDay(pictureOfDay.apply{
//            this.createdAt = System.currentTimeMillis()
//        })
//    }
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