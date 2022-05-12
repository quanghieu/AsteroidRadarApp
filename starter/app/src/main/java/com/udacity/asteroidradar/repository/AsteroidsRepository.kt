package com.udacity.asteroidradar.repository

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.asDatabaseModel
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.main.MainViewModel
import com.udacity.asteroidradar.networks.AsteroidApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.annotation.Target as Target

class AsteroidsRepository(val asteroidDatabase: AsteroidDatabase) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = dateFormat.format(Calendar.getInstance().time)

    val asteroidAll : LiveData<List<Asteroid>> = Transformations.map(asteroidDatabase.asteroidDao.getAllAsteroidsAndSort()) {
        it.asDomainModel()
    }

    val asteroidToday : LiveData<List<Asteroid>> = Transformations.map(asteroidDatabase.asteroidDao.getAsteroidsToday(currentDate)) {
        it.asDomainModel()
    }

    val asteroidWeek : LiveData<List<Asteroid>> = Transformations.map(asteroidDatabase.asteroidDao.getAllAsteroidsWeekAndSort()) {
        it.asDomainModel()
    }

    private val _refreshImageDone = MutableLiveData<Boolean>()
    val refreshImageDone : LiveData<Boolean>
        get() = _refreshImageDone

    suspend fun refreshImageOfTheDay(application: Application) : String? {
        try {
            val imageJson =
                JSONObject(AsteroidApi.retrofitAPIService.getImageOfTheDay(Constants.API_KEY))
            Log.d("HIEU", imageJson.toString())
            val sharedPreferences =
                application.getSharedPreferences("ImageOfTheDay", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("media_type", imageJson.getString("media_type"))
                putString("title", imageJson.getString("title"))
                commit()
            }
            return imageJson.getString("url")
        }
        catch (e : java.lang.Exception) {
            e.printStackTrace()
            return null
        }
    }

    class ImageOfTheDayTarget(val url: String) : com.squareup.picasso.Target {
        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            val file = File(url)
            try {
                file.createNewFile()
                val outputStream = FileOutputStream(file)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
            }
            catch (e : java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {

        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

        }

    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            Log.d("HIEU","Prepare to refresh data")
            try {
                val asteroidString = AsteroidApi.retrofitAPIService.getAsteroids(
                    currentDate,
                    Constants.API_KEY
                )
                Log.d("HIEU", "Refresh data done")
                val jsonString = JSONObject(asteroidString)
                val asteroidList = parseAsteroidsJsonResult(jsonString).toList()
                asteroidDatabase.asteroidDao.insertAll(
                    *asteroidList.asDatabaseModel().toTypedArray()
                )
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


class ImageOfTheDay(val url : String?, val media_type : String?, val title : String?, val cache : String?)