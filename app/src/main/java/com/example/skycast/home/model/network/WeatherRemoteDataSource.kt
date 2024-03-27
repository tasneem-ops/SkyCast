package com.example.skycast.home.model.network

import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
class WeatherRemoteDataSource private constructor(){
    companion object{
        @Volatile
        private var INSTANCE: WeatherRemoteDataSource? = null
        fun getInstance (): WeatherRemoteDataSource {
            return INSTANCE ?: synchronized(this){
                val instance = WeatherRemoteDataSource()
                INSTANCE = instance
                instance
            }
        }
    }
    fun getDailyForecast(latLng: LatLng, apiKey : String, units : String, lang : String) : Flow<List<DailyWeather>> {
        return flow{
            val responseBody = ForecastRetrofitHelper.retrofitService
                .getWeatherForecast(latLng.latitude, latLng.longitude, apiKey, units, lang)
                .body()
            val dailyWeatherList = arrayListOf<DailyWeather>()
            responseBody?.daily?.forEach {
                val dailyWeather = DailyWeather(it.dt, it.moon_phase, it.summary, it.temp.min, it.temp.max, it.pressure,
                    it.humidity, it.wind_speed, it.weather[0].id, it.weather[0].description, it.clouds, it.uvi, lang)
                dailyWeatherList.add(dailyWeather)
            }
            emit(dailyWeatherList)
        }
    }

    fun getHourlyForecast(latLng: LatLng, apiKey: String, units: String, lang: String) : Flow<List<HourlyWeather>>{
        return flow{
            val responseBody = ForecastRetrofitHelper.retrofitService
                .getWeatherForecast(latLng.latitude, latLng.longitude, apiKey, units, lang)
                .body()
            val hourlyWeatherList = arrayListOf<HourlyWeather>()
            responseBody?.hourly?.forEach {
                val hourlyWeather = HourlyWeather(it.dt, it.temp, it.feels_like, it.pressure, it.humidity,
                    it.uvi, it.clouds, it.visibility, it.wind_speed, it.weather[0].id, it.weather[0].description, lang)
                hourlyWeatherList.add(hourlyWeather)
            }
            emit(hourlyWeatherList)
        }
    }

    fun getCurrentForecast(latLng: LatLng, apiKey: String, units: String, lang: String) : Flow<HourlyWeather?>{
        return flow{
            val responseBody = ForecastRetrofitHelper.retrofitService
                .getWeatherForecast(latLng.latitude, latLng.longitude, apiKey, units, lang)
                .body()
            var current : HourlyWeather? = null
            responseBody?.current?.let {
                current = HourlyWeather(it.dt, it.temp, it.feels_like, it.pressure, it.humidity,
                    it.uvi, it.clouds, it.visibility, it.wind_speed, it.weather[0].id, it.weather[0].description, lang)
            }
            emit(current)
        }
    }
}