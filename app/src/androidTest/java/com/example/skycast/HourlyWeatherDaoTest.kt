package com.example.skycast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.skycast.home.model.db.WeatherDB
import com.example.skycast.home.model.dto.HourlyWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class HourlyWeatherDaoTest {
    @get:Rule
    val instantExcuterRule = InstantTaskExecutorRule()
    lateinit var database: WeatherDB
    @Before
    fun initDB(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDB::class.java).build()
    }
    @After
    fun closeDB() = database.close()

    @Test
    fun insertAll_getAll_sameData( ) = runBlockingTest{
        //Given
        val dao = database.getHourlyWeatherDao()

        //When insert All Hourly List
        dao.insertAll(*hourlyWeatherList.toTypedArray())
        val result = dao.getAll()

        // Assert That Data is saved in db
        assertThat(result, `is`(notNullValue()))
        assertThat(result.first(), `is`(hourlyWeatherList))
    }

    @Test
    fun getHourlyWeatherForecast_get24HoursOrdered( ) = runBlockingTest{
        //Given database with data in it
        val dao = database.getHourlyWeatherDao()
        dao.insertAll(*hourlyWeatherList.toTypedArray())

        //When get All Hourly List
        val result = dao.getHourlyWeatherForecast(1616983200, "en")
        val result2 = dao.getHourlyWeatherForecast(1616994000, "en")


        // Assert That Data is as expected
        assertThat(result, `is`(notNullValue()))
        assertThat(result.first().size, `is`(24))
        assertThat(result.first(), `is`(hourlyWeatherList.take(24)))
        assertThat(isSorted(result2.first()), `is`(true))

        assertThat(result2, `is`(notNullValue()))
        assertThat(result2.first().size, `is`(24))
        assertThat(result2.first(), `is`(hourlyWeatherList.takeLast(27).take(24)))
        assertThat(isSorted(result2.first()), `is`(true))
    }
    @Test
    fun getCurrentWeatherForecast_getCorrectData( ) = runBlockingTest{
        //Given database with data in it
        val dao = database.getHourlyWeatherDao()
        dao.insertAll(*hourlyWeatherList.toTypedArray())

        //When get All Current Weather
        val result = dao.getCurrentWeatherForecast(1617008400, "en")

        // Assert That Data is as expected
        assertThat(result, `is`(notNullValue()))
        assertThat(result.first(), `is`(hourlyWeatherList.get(7)))
    }

    @Test
    fun clear_getAllData_empty() = runBlockingTest{
        //Given Dao with Data in it
        val dao = database.getHourlyWeatherDao()
        dao.insertAll(*hourlyWeatherList.toTypedArray())

        //When Clear
        dao.clear()
        val result = dao.getAll()

        // Assert That Data is saved in db
        assertThat(result, `is`(notNullValue()))
        assertThat(result.first(), `is`(emptyList()))
    }

    val hourlyWeatherList = arrayListOf(
        HourlyWeather(1616983200, 15.2, 13.5, 1016, 72, 3.4, 20, 10000, 3.1, 801, "Cloudy", "en"),
        HourlyWeather(1616986800, 15.4, 13.7, 1015, 70, 3.2, 20, 10000, 3.0, 801, "Cloudy", "en"),
        HourlyWeather(1616990400, 15.6, 13.9, 1015, 69, 3.0, 20, 10000, 2.9, 801, "Cloudy", "en"),
        HourlyWeather(1616994000, 15.8, 14.1, 1014, 68, 2.8, 20, 10000, 2.8, 801, "Cloudy", "en"),
        HourlyWeather(1616997600, 16.0, 14.3, 1013, 67, 2.6, 20, 10000, 2.7, 801, "Cloudy", "en"),
        HourlyWeather(1617001200, 16.2, 14.5, 1013, 66, 2.4, 20, 10000, 2.6, 801, "Cloudy", "en"),
        HourlyWeather(1617004800, 16.4, 14.7, 1012, 65, 2.2, 20, 10000, 2.5, 801, "Cloudy", "en"),
        HourlyWeather(1617008400, 16.6, 14.9, 1011, 64, 2.0, 20, 10000, 2.4, 801, "Cloudy", "en"),
        HourlyWeather(1617012000, 16.8, 15.1, 1011, 63, 1.8, 20, 10000, 2.3, 801, "Cloudy", "en"),
        HourlyWeather(1617015600, 17.0, 15.3, 1010, 62, 1.6, 20, 10000, 2.2, 801, "Cloudy", "en"),
        // Add more objects as needed
        HourlyWeather(1617019200, 17.2, 15.5, 1009, 61, 1.4, 20, 10000, 2.1, 801, "Cloudy", "en"),
        HourlyWeather(1617022800, 17.4, 15.7, 1009, 60, 1.2, 20, 10000, 2.0, 801, "Cloudy", "en"),
        HourlyWeather(1617026400, 17.6, 15.9, 1008, 59, 1.0, 20, 10000, 1.9, 801, "Cloudy", "en"),
        HourlyWeather(1617030000, 17.8, 16.1, 1007, 58, 0.8, 20, 10000, 1.8, 801, "Cloudy", "en"),
        HourlyWeather(1617033600, 18.0, 16.3, 1007, 57, 0.6, 20, 10000, 1.7, 801, "Cloudy", "en"),
        HourlyWeather(1617037200, 18.2, 16.5, 1006, 56, 0.4, 20, 10000, 1.6, 801, "Cloudy", "en"),
        HourlyWeather(1617040800, 18.4, 16.7, 1005, 55, 0.2, 20, 10000, 1.5, 801, "Cloudy", "en"),
        HourlyWeather(1617044400, 18.6, 16.9, 1005, 54, 0.0, 20, 10000, 1.4, 801, "Cloudy", "en"),
        HourlyWeather(1617048000, 18.8, 17.1, 1004, 53, 0.2, 20, 10000, 1.3, 801, "Cloudy", "en"),
        HourlyWeather(1617051600, 19.0, 17.3, 1003, 52, 0.4, 20, 10000, 1.2, 801, "Cloudy", "en"),
        HourlyWeather(1617055200, 19.2, 17.5, 1003, 51, 0.6, 20, 10000, 1.1, 801, "Cloudy", "en"),
        HourlyWeather(1617058800, 19.4, 17.7, 1002, 50, 0.8, 20, 10000, 1.0, 801, "Cloudy", "en"),
        HourlyWeather(1617062400, 19.6, 17.9, 1001, 49, 1.0, 20, 10000, 0.9, 801, "Cloudy", "en"),
        HourlyWeather(1617066000, 19.8, 18.1, 1001, 48, 1.2, 20, 10000, 0.8, 801, "Cloudy", "en"),
        HourlyWeather(1617069600, 20.0, 18.3, 1000, 47, 1.4, 20, 10000, 0.7, 801, "Cloudy", "en"),
        HourlyWeather(1617073200, 20.2, 18.5, 999, 46, 1.6, 20, 10000, 0.6, 801, "Cloudy", "en"),
        HourlyWeather(1617076800, 20.4, 18.7, 999, 45, 1.8, 20, 10000, 0.5, 801, "Cloudy", "en"),
        HourlyWeather(1617080400, 20.6, 18.9, 998, 44, 2.0, 20, 10000, 0.4, 801, "Cloudy", "en"),
        HourlyWeather(1617084000, 20.8, 19.1, 997, 43, 2.2, 20, 10000, 0.3, 801, "Cloudy", "en"),
        HourlyWeather(1617087600, 21.0, 19.3, 997, 42, 2.4, 20, 10000, 0.2, 801, "Cloudy", "en")
    )
}

private fun isSorted(list : List<HourlyWeather>) : Boolean{
    for (i in 0 until list.size - 1) {
        if (list[i].dt > list[i + 1].dt) {
            return false
        }
    }
    return true
}