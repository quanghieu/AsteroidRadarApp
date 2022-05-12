package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {
    @Query("select * from asteroidentity where closeApproachDate = :date")
    fun getAsteroidsToday(date : String) : LiveData<List<AsteroidEntity>>

    @Query("select * from asteroidentity where closeApproachDate <= DATE('now', '+7 day') order by closeApproachDate")
    fun getAllAsteroidsWeekAndSort() :LiveData<List<AsteroidEntity>>

    @Query("select * from asteroidentity order by closeApproachDate")
    fun getAllAsteroidsAndSort() :LiveData<List<AsteroidEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: AsteroidEntity)

    @Query("select count(*) from asteroidentity")
    fun getCount() : Int
}

@Database(entities = [AsteroidEntity::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase(){
    abstract val asteroidDao : AsteroidDao
}

private lateinit var INSTANCE : AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if(!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context, AsteroidDatabase::class.java, "asteroid").build()
        }
    }
    return INSTANCE
}

fun List<AsteroidEntity>.asDomainModel() : List<Asteroid> {
    return map {
        Asteroid(it.id, it.codename, it.closeApproachDate, it.absoluteMagnitude, it.estimatedDiameter, it.relativeVelocity, it.distanceFromEarth, it.isPotentiallyHazardous)
    }
}