package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.networks.AsteroidAPIService
import com.udacity.asteroidradar.networks.AsteroidApi
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.repository.ImageOfTheDay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val asteroidRepository = AsteroidsRepository(getDatabase(application))

    private val _imageOfTheDay = MutableLiveData<ImageOfTheDay>()

    val imageOfTheDay: LiveData<ImageOfTheDay>
        get() = _imageOfTheDay

    lateinit var asteroidList: LiveData<List<Asteroid>>

    private val _loadImageOfTheDayDone = MutableLiveData<Boolean>()
    val loadImageOfTheDayDone: LiveData<Boolean>
        get() = _loadImageOfTheDayDone

    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            val url = asteroidRepository.refreshImageOfTheDay(application)
            loadImageOfTheDay(application, url)
        }
        asteroidListFilter(FilterList.ALL)
    }

    fun loadImageOfTheDay(application: Application, url: String?) {
        var cache: String?
        if (url == null) {
            cache = application.filesDir.absolutePath + Constants.imageofthedayName
        } else {
            cache = null
        }

        val sharedPreferences =
            application.getSharedPreferences("ImageOfTheDay", Context.MODE_PRIVATE)
        val media_type = sharedPreferences.getString("media_type", "")
        val title = sharedPreferences.getString("title", "")
        val img = ImageOfTheDay(url, media_type, title, cache)
        _imageOfTheDay.value = img

        //cache image
        Picasso.with(application).load(url)
            .into(AsteroidsRepository.ImageOfTheDayTarget(application.filesDir.absolutePath + Constants.imageofthedayName))
        Log.d("HIEU", "Cache done")
    }

    fun asteroidListFilter(filterList: FilterList) {
        Log.d("Filtering", filterList.toString())
        when (filterList) {
            FilterList.ALL -> asteroidList = asteroidRepository.asteroidAll
            FilterList.TODAY -> asteroidList = asteroidRepository.asteroidToday
            FilterList.WEEK -> asteroidList = asteroidRepository.asteroidWeek
        }
    }

}

enum class FilterList {
    ALL,
    WEEK,
    TODAY
}