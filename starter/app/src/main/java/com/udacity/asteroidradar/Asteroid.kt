package com.udacity.asteroidradar

import android.os.Parcelable
import androidx.lifecycle.Transformations.map
import com.udacity.asteroidradar.database.AsteroidEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Asteroid(val id: Long, val codename: String, val closeApproachDate: String,
                    val absoluteMagnitude: Double, val estimatedDiameter: Double,
                    val relativeVelocity: Double, val distanceFromEarth: Double,
                    val isPotentiallyHazardous: Boolean) : Parcelable

fun List<Asteroid>.asDatabaseModel() : List<AsteroidEntity> {
    return map {
        AsteroidEntity(it.id, it.codename, it.closeApproachDate, it.absoluteMagnitude, it.estimatedDiameter, it.relativeVelocity, it.distanceFromEarth, it.isPotentiallyHazardous)
    }
}