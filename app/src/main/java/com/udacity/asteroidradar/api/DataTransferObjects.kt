package com.udacity.asteroidradar.api


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.DatabasePictureOfDay

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

@JsonClass(generateAdapter = true)
data class NetworkPictureOfDay(@Json(name = "media_type")
                               val mediaType: String,
                               val title: String,
                               val url: String)



@JsonClass(generateAdapter = true)
data class NetworkAsteroid(val id: Long, val codename: String, val closeApproachDate: String,
                           val absoluteMagnitude: Double, val estimatedDiameter: Double,
                           val relativeVelocity: Double, val distanceFromEarth: Double,
                           val isPotentiallyHazardous: Boolean)


/**
 * Extension function Convert Network results to domain objects
 */
//fun ArrayList<NetworkAsteroid>.asDomainModel(): List<Asteroid> {
//    return map {
//        Asteroid(
//                id = it.id,
//                codename = it.codename,
//                closeApproachDate = it.closeApproachDate,
//                absoluteMagnitude = it.absoluteMagnitude,
//                estimatedDiameter = it.estimatedDiameter,
//                relativeVelocity = it.relativeVelocity,
//                distanceFromEarth = it.distanceFromEarth,
//                isPotentiallyHazardous = it.isPotentiallyHazardous
//        )
//    }
//}

/**
 * Extension function Convert Network results to database objects
 * toTypedArray and Array here because the insert from the dao demands a vararg of DatabaseAsteroids
 */
fun ArrayList<NetworkAsteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return map {
        DatabaseAsteroid(
                id = it.id,
                codename = it.codename,
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                relativeVelocity = it.relativeVelocity,
                distanceFromEarth = it.distanceFromEarth,
                isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}


/**
 * Extension function Convert Network results to database objects
 */
fun NetworkPictureOfDay.asDatabaseModel(): DatabasePictureOfDay {
    return DatabasePictureOfDay(
                mediaType = this.mediaType,
                title = this.title,
                url = this.url,
                createdAt = System.currentTimeMillis())

}