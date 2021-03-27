package com.udacity.asteroidradar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay

/**
 * In order to separate concerns we have a dedicated Database model [DatabaseAsteroid] and a
 * a dedicated domain model [Asteroid]
 */
@Entity
data class DatabaseAsteroid constructor(
        @PrimaryKey
        val id: Long,
        val codename: String,
        val closeApproachDate: String,
        val absoluteMagnitude: Double,
        val estimatedDiameter: Double,
        val relativeVelocity: Double,
        val distanceFromEarth: Double,
        val isPotentiallyHazardous: Boolean)


@Entity
data class DatabasePictureOfDay constructor(
        @PrimaryKey
        val url: String,

        @ColumnInfo(name = "created_at")
        val createdAt: Long,

        val mediaType: String,
        val title: String
        )


/**
 * Kotlin extension function to get domain models from the database models
 */
fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
                id = it.id,
                codename = it.codename,
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                relativeVelocity = it.relativeVelocity,
                distanceFromEarth = it.distanceFromEarth,
                isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

/**
 * Kotlin extension function to get domain models from the database models
 */
fun DatabasePictureOfDay.asDomainModel(): PictureOfDay {
    return PictureOfDay(
            url = this.url,
            mediaType = this.mediaType,
            title = this.title)
    }
