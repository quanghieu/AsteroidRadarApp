package com.udacity.asteroidradar.work

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException

class RefreshDataWork(appContext : Context, params : WorkerParameters) : CoroutineWorker(appContext, params){
    companion object {
        const val WORK_NAME = "RefreshDataWork"
    }

    override suspend fun doWork(): Result {
        val asteroidsRepository = AsteroidsRepository(getDatabase(applicationContext))
        return try {
            asteroidsRepository.refreshAsteroids()
            Result.success()
        } catch (exception : HttpException) {
            Result.failure()
        }
    }


}