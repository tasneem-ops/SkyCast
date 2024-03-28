package com.example.skycast.model.repository

import com.example.skycast.alert.model.dto.Alert
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.favorites.model.dto.FavDTO
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.location.model.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherRepository() : IWeatherRepository {
    val localDailyWeatherList = arrayListOf<DailyWeather>()
    val localHourlyWeatherList = arrayListOf<HourlyWeather>()
    var localCurrentWeather : HourlyWeather? = null

    val alertlist = arrayListOf<AlertDTO>()
    val favList = arrayListOf<FavDTO>()

    override fun getDailyWeather(
        latLng: LatLng,
        apiKey: String,
        lang: String,
        forceUpdate: Boolean
    ): Flow<List<DailyWeather>> {
        val dailyWeather6 = DailyWeather(1615008000, 0.5, "Cloudy", 20.0, 30.0, 1010, 65, 9.0, 802, "Scattered clouds", 60, 6.0, "en")
        val dailyWeather7 = DailyWeather(1615094400, 0.75, "Rainy", 18.0, 28.0, 1015, 80, 6.5, 500, "Light rain", 80, 4.5, "en")
        val dailyWeather8 = DailyWeather(1615180800, 0.25, "Sunny", 25.0, 35.0, 1005, 70, 10.0, 801, "Few clouds", 40, 7.5, "en")
        val dailyWeather9 = DailyWeather(1615267200, 0.0, "Partly cloudy", 22.0, 32.0, 1008, 75, 8.0, 500, "Moderate rain", 90, 5.0, "en")
        val dailyWeather10 = DailyWeather(1615353600, 0.9, "Mostly sunny", 30.0, 40.0, 1000, 60, 11.0, 800, "Clear sky", 10, 8.0, "en")

        if(forceUpdate){
            return flow {
                emit(listOf(dailyWeather6, dailyWeather7, dailyWeather8, dailyWeather9, dailyWeather10))
            }
        }
        else{
            return flow<List<DailyWeather>> {
                emit(localDailyWeatherList)
            }
        }
    }

    override fun getHourlyWeather(
        latLng: LatLng,
        apiKey: String,
        lang: String,
        forceUpdate: Boolean
    ): Flow<List<HourlyWeather>> {
        val hourlyWeather6 = HourlyWeather(1615008000, 22.0, 20.0, 1010, 75, 6.0, 60, 10000, 8.0, 802, "Scattered clouds", "en")
        val hourlyWeather7 = HourlyWeather(1615094400, 18.0, 16.0, 1015, 80, 4.5, 80, 10000, 6.5, 500, "Light rain", "en")
        val hourlyWeather8 = HourlyWeather(1615180800, 25.0, 23.0, 1012, 70, 7.5, 40, 10000, 9.5, 801, "Few clouds", "en")
        val hourlyWeather9 = HourlyWeather(1615267200, 20.0, 18.0, 1013, 85, 5.0, 90, 10000, 7.0, 501, "Moderate rain", "en")
        val hourlyWeather10 = HourlyWeather(1615353600, 28.0, 26.0, 1010, 60, 8.0, 10, 10000, 11.0, 800, "Clear sky", "en")
        if (forceUpdate){
            return flow {
                emit(listOf(hourlyWeather6, hourlyWeather7, hourlyWeather8, hourlyWeather9, hourlyWeather10))
            }
        }
        else{
            return flow {
                emit(localHourlyWeatherList)
            }
        }
    }

    override fun getCurrentWeather(
        latLng: LatLng,
        apiKey: String,
        lang: String,
        forceUpdate: Boolean
    ): Flow<HourlyWeather?> {
        val hourlyWeather6 = HourlyWeather(1615008000, 22.0, 20.0, 1010, 75, 6.0, 60, 10000, 8.0, 802, "Scattered clouds", "en")
        if(forceUpdate){
            return flow {
                emit(hourlyWeather6)
            }
        }
        else{
            return flow {
                emit(localCurrentWeather)
            }
        }
    }

    override suspend fun updateWeatherCache(latLng: LatLng, apiKey: String) {
        localDailyWeatherList.clear()
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

        localDailyWeatherList.addAll(listOf(dummyWeather1, dummyWeather2, dummyWeather3, dummyWeather4, dummyWeather5))
        localHourlyWeatherList.addAll(listOf(hourlyWeather1, hourlyWeather2, hourlyWeather3, hourlyWeather4, hourlyWeather5))
        localCurrentWeather = hourlyWeather1
    }

    override fun getAlerts(): Flow<List<AlertDTO>> {
        return flow {
            emit(alertlist)
        }
    }

    override suspend fun addAlert(alertDTO: AlertDTO): Long {
        alertlist.add(alertDTO)
        return 1L
    }

    override suspend fun deleteAlert(alertDTO: AlertDTO): Int {
        alertlist.remove(alertDTO)
        return 1
    }

    override suspend fun addFav(favDTO: FavDTO): Long {
        favList.add(favDTO)
        return 1L
    }

    override fun getAllFav(): Flow<List<FavDTO>> {
        return flow{
            emit(favList)
        }
    }

    override suspend fun deleteFav(favDTO: FavDTO): Int {
        favList.remove(favDTO)
        return 1
    }

    override fun getAlert(latLng: LatLng, apiKey: String): Flow<Alert> {
        return flow {

        }
    }

    override fun getSearchSuggestions(
        query: String,
        format: String,
        lang: String,
        limit: Int
    ): Flow<List<Place>> {
        TODO("Not yet implemented")
    }
}