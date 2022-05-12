package com.udacity.asteroidradar.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AsteroidEntity constructor(
    @PrimaryKey val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) {
}