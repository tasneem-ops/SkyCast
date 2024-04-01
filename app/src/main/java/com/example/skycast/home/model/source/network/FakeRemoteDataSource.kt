package com.example.skycast.home.model.source.network

import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.location.model.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRemoteDataSource : WeatherRemoteDataSource {
    val cityNames = listOf(
        "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
        "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose",
        "Austin", "Jacksonville", "San Francisco", "Fort Worth", "Charlotte",
        "Seattle", "Denver", "Washington", "Boston", "El Paso",
        "Detroit", "Nashville", "Portland", "Memphis", "Oklahoma City",
        "Las Vegas", "Louisville", "Baltimore", "Milwaukee", "Albuquerque",
        "Tucson", "Fresno", "Sacramento", "Mesa", "Kansas City",
        "Atlanta", "Long Beach", "Colorado Springs", "Miami"
    )
    val places = cityNames.map { cityName ->
        Place(lat = null, lon = null, name = cityName, displayName = null)
    }
    override fun getDailyForecast(
        latLng: LatLng,
        apiKey: String,
        units: String,
        lang: String
    ): Flow<List<DailyWeather>> {
        if (lang == "en"){
            val dailyWeather6 = DailyWeather(1615008000, 0.5, "Cloudy", 20.0, 30.0, 1010, 65, 9.0, 802, "Scattered clouds", 60, 6.0, "en")
            val dailyWeather7 = DailyWeather(1615094400, 0.75, "Rainy", 18.0, 28.0, 1015, 80, 6.5, 500, "Light rain", 80, 4.5, "en")
            val dailyWeather8 = DailyWeather(1615180800, 0.25, "Sunny", 25.0, 35.0, 1005, 70, 10.0, 801, "Few clouds", 40, 7.5, "en")
            val dailyWeather9 = DailyWeather(1615267200, 0.0, "Partly cloudy", 22.0, 32.0, 1008, 75, 8.0, 500, "Moderate rain", 90, 5.0, "en")
            val dailyWeather10 = DailyWeather(1615353600, 0.9, "Mostly sunny", 30.0, 40.0, 1000, 60, 11.0, 800, "Clear sky", 10, 8.0, "en")
            return flow {
                emit(listOf(dailyWeather6, dailyWeather7, dailyWeather8, dailyWeather9, dailyWeather10))
            }
        }
        else{
            return flow {  }
        }

    }

    override fun getHourlyForecast(
        latLng: LatLng,
        apiKey: String,
        units: String,
        lang: String
    ): Flow<List<HourlyWeather>> {
        if(lang == "en"){
            val hourlyWeather6 = HourlyWeather(1615008000, 22.0, 20.0, 1010, 75, 6.0, 60, 10000, 8.0, 802, "Scattered clouds", "en")
            val hourlyWeather7 = HourlyWeather(1615094400, 18.0, 16.0, 1015, 80, 4.5, 80, 10000, 6.5, 500, "Light rain", "en")
            val hourlyWeather8 = HourlyWeather(1615180800, 25.0, 23.0, 1012, 70, 7.5, 40, 10000, 9.5, 801, "Few clouds", "en")
            val hourlyWeather9 = HourlyWeather(1615267200, 20.0, 18.0, 1013, 85, 5.0, 90, 10000, 7.0, 501, "Moderate rain", "en")
            val hourlyWeather10 = HourlyWeather(1615353600, 28.0, 26.0, 1010, 60, 8.0, 10, 10000, 11.0, 800, "Clear sky", "en")
            return flow {
                emit(listOf(hourlyWeather6, hourlyWeather7, hourlyWeather8, hourlyWeather9, hourlyWeather10))
            }
        }
        else{
            return flow {  }
        }

    }

    override fun getCurrentForecast(
        latLng: LatLng,
        apiKey: String,
        units: String,
        lang: String
    ): Flow<HourlyWeather?> {
        if(lang == "en"){
                val currentWeather = HourlyWeather(1615008000, 22.0, 20.0, 1010, 75, 6.0, 60, 10000, 8.0, 802, "Scattered clouds", "en")
                return flow {
                    emit(currentWeather)
                }
            }
        else{
            return flow {  }
        }
    }

    override fun getSearchSuggestions(
        query: String,
        format: String,
        lang: String,
        limit: Int
    ): Flow<List<Place>> {
        return flow {
            val filtered = places.filter {
                it.name.contentEquals(query)
            }
            emit(filtered)
        }
    }
}