package com.example.skycast.home.model.source.local

import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.favorites.model.dto.FavDTO
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource : WeatherLocalDataSource {
    var localDailyWeatherList = arrayListOf<DailyWeather>()
    var localHourlyWeatherList = arrayListOf<HourlyWeather>()
    var localCurrentWeather : HourlyWeather? = null
    init {
        val dummyWeather1 = DailyWeather(1614576000, 0.25, "Partly cloudy", 15.0, 25.0, 1012, 70, 10.5, 800, "Clear sky", 20, 7.5, "en")
        val dummyWeather2 = DailyWeather(1614662400, 0.75, "Mostly sunny", 18.0, 28.0, 1010, 65, 12.0, 801, "Few clouds", 30, 6.8, "en")
        val dummyWeather3 = DailyWeather(1614748800, 0.5, "Sunny", 20.0, 30.0, 1015, 60, 9.0, 800, "Clear sky", 10, 8.0, "en")
        val dummyWeather4 = DailyWeather(1614835200, 0.0, "Cloudy", 16.0, 22.0, 1018, 75, 8.5, 803, "Broken clouds", 70, 5.5, "en")
        val dummyWeather5 = DailyWeather(1614921600, 0.9, "Partly cloudy", 17.0, 27.0, 1013, 68, 11.0, 801, "Few clouds", 25, 7.2, "en")
        val hourlyWeather1 = HourlyWeather(1614576000, 25.0, 23.0, 1012, 70, 7.5, 20, 10000, 10.5, 800, "Clear sky", "en")
        val hourlyWeather2 = HourlyWeather(1614662400, 28.0, 26.0, 1010, 65, 6.8, 30, 10000, 12.0, 801, "Few clouds", "en")
        val hourlyWeather3 = HourlyWeather(1614748800, 30.0, 28.0, 1015, 60, 8.0, 10, 10000, 9.0, 800, "Clear sky", "en")
        val hourlyWeather4 = HourlyWeather(1614835200, 22.0, 20.0, 1018, 75, 5.5, 70, 10000, 8.5, 803, "Broken clouds", "en")
        val hourlyWeather5 = HourlyWeather(1614921600, 27.0, 25.0, 1013, 68, 7.2, 25, 10000, 11.0, 801, "Few clouds", "en")

        localDailyWeatherList = arrayListOf(dummyWeather1, dummyWeather2, dummyWeather3, dummyWeather4, dummyWeather5)
        localHourlyWeatherList = arrayListOf(hourlyWeather1, hourlyWeather2, hourlyWeather3, hourlyWeather4, hourlyWeather5)
        localCurrentWeather = localHourlyWeatherList.get(0)
    }

    val alertlist = arrayListOf<AlertDTO>()
    val favList = arrayListOf<FavDTO>()

    override fun getDailyWeather(dt: Int, lang: String): Flow<List<DailyWeather>> {
        return flow {
            emit(localDailyWeatherList)
        }
    }

    override fun getAllDailyWeather(): Flow<List<DailyWeather>> {
        return flow {
            emit(localDailyWeatherList)
        }
    }

    override suspend fun clearDailyWeather(): Int {
        val size = localDailyWeatherList.size
        localDailyWeatherList.clear()
        return  size
    }

    override suspend fun insertDailyWeather(vararg dailyWeather: DailyWeather): List<Long> {
        localDailyWeatherList.addAll(dailyWeather)
        return emptyList()
    }

    override fun getHourlyWeatherForecast(dt: Int, lang: String): Flow<List<HourlyWeather>> {
        return flow {
            emit(localHourlyWeatherList)
        }
    }

    override fun getAllHourlyWeatherForecast(): Flow<List<HourlyWeather>> {
        return flow {
            localHourlyWeatherList
        }
    }

    override fun getCurrentWeatherForecast(dt: Int, lang: String): Flow<HourlyWeather> {
        return flow {
            localCurrentWeather?.let { emit(it) }
        }
    }

    override suspend fun insertHourlyWeather(vararg list: HourlyWeather): List<Long> {
        localHourlyWeatherList.addAll(list)
        localCurrentWeather = localHourlyWeatherList.get(0)
        return emptyList()
    }

    override suspend fun clearHourlyWeather(): Int {
        val size = localHourlyWeatherList.size
        localHourlyWeatherList.clear()
        return size
    }
//    override suspend fun addFav(favDTO: FavDTO): Long {
//        favList.add(favDTO)
//        return 1L
//    }
//
//    override fun getAllFav(): Flow<List<FavDTO>> {
//        return flow {
//            emit(favList)
//        }
//    }
//
//    override suspend fun deleteFav(favDTO: FavDTO): Int {
//        favList.remove(favDTO)
//        return 1
//    }
}