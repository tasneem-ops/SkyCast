package com.example.skycast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.skycast.home.model.dto.DailyWeather
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.home.model.source.local.FakeLocalDataSource
import com.example.skycast.home.model.source.local.WeatherLocalDataSource
import com.example.skycast.home.model.source.network.FakeRemoteDataSource
import com.example.skycast.home.model.source.network.WeatherRemoteDataSource
import com.example.skycast.home.model.source.repository.WeatherRepository
import com.example.skycast.home.model.source.repository.WeatherRepositoryImpl
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherRepositoryTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    @get:Rule
    val instantTask = InstantTaskExecutorRule()

    lateinit var remoteDataSource: WeatherRemoteDataSource
    lateinit var localDataSource: WeatherLocalDataSource
    lateinit var repository: WeatherRepository

    lateinit var remoteDailyList : List<DailyWeather>
    lateinit var remoteHourlyList : List<HourlyWeather>
    lateinit var currentRemoteWeather : HourlyWeather
    lateinit var localDailyWeatherList : List<DailyWeather>
    lateinit var localHourlyWeatherList : List<HourlyWeather>
    lateinit var localCurrentWeather : HourlyWeather
    @Before
    fun initData(){
        val remoteDailyWeather6 = DailyWeather(1615008000, 0.5, "Cloudy", 20.0, 30.0, 1010, 65, 9.0, 802, "Scattered clouds", 60, 6.0, "en")
        val remoteDailyWeather7 = DailyWeather(1615094400, 0.75, "Rainy", 18.0, 28.0, 1015, 80, 6.5, 500, "Light rain", 80, 4.5, "en")
        val remoteDailyWeather8 = DailyWeather(1615180800, 0.25, "Sunny", 25.0, 35.0, 1005, 70, 10.0, 801, "Few clouds", 40, 7.5, "en")
        val remoteDailyWeather9 = DailyWeather(1615267200, 0.0, "Partly cloudy", 22.0, 32.0, 1008, 75, 8.0, 500, "Moderate rain", 90, 5.0, "en")
        val remoteDailyWeather10 = DailyWeather(1615353600, 0.9, "Mostly sunny", 30.0, 40.0, 1000, 60, 11.0, 800, "Clear sky", 10, 8.0, "en")
        remoteDailyList = listOf<DailyWeather>(remoteDailyWeather6, remoteDailyWeather7, remoteDailyWeather8, remoteDailyWeather9, remoteDailyWeather10)
        val remoteHourlyWeather6 = HourlyWeather(1615008000, 22.0, 20.0, 1010, 75, 6.0, 60, 10000, 8.0, 802, "Scattered clouds", "en")
        val remoteHourlyWeather7 = HourlyWeather(1615094400, 18.0, 16.0, 1015, 80, 4.5, 80, 10000, 6.5, 500, "Light rain", "en")
        val remoteHourlyWeather8 = HourlyWeather(1615180800, 25.0, 23.0, 1012, 70, 7.5, 40, 10000, 9.5, 801, "Few clouds", "en")
        val remoteHourlyWeather9 = HourlyWeather(1615267200, 20.0, 18.0, 1013, 85, 5.0, 90, 10000, 7.0, 501, "Moderate rain", "en")
        val remoteHourlyWeather10 = HourlyWeather(1615353600, 28.0, 26.0, 1010, 60, 8.0, 10, 10000, 11.0, 800, "Clear sky", "en")
        remoteHourlyList = listOf<HourlyWeather>(remoteHourlyWeather6, remoteHourlyWeather7, remoteHourlyWeather8, remoteHourlyWeather9, remoteHourlyWeather10)
        currentRemoteWeather = remoteHourlyWeather6

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

        localDailyWeatherList = listOf(dummyWeather1, dummyWeather2, dummyWeather3, dummyWeather4, dummyWeather5)
        localHourlyWeatherList = listOf(hourlyWeather1, hourlyWeather2, hourlyWeather3, hourlyWeather4, hourlyWeather5)
        localCurrentWeather = hourlyWeather1
    }

    @Before
    fun initializeRepository(){
        remoteDataSource = FakeRemoteDataSource()
        localDataSource = FakeLocalDataSource()
        repository = WeatherRepositoryImpl.getInstance(remoteDataSource, localDataSource, Dispatchers.Main)
    }

    @Test
    fun getDailyWeather_forceUpdateTrue_remoteData() = runTest{
        //Given Created Repository

        //When
        val data = repository.getDailyWeather(LatLng(20.0,10.0),"", "en", true)

        //Assert that data is not empty and is equal to remote Data
        assertThat(data.first(), `is`(notNullValue()))
        assertThat(data.first().size, `is`(remoteDailyList.size))
        assertThat(data.first(), `is`(remoteDailyList))
    }
    @Test
    fun getDailyWeather_forceUpdateFalse_localData() = runTest{
        //Given Created Repository

        //When
        val data = repository.getDailyWeather(LatLng(20.0,10.0),"", "en", false)

        //Assert that data is not empty and is equal to local Data
        assertThat(data.first(), `is`(notNullValue()))
        assertThat(data.first().size, `is`(localDailyWeatherList.size))
        assertThat(data.first(), `is`(localDailyWeatherList))
    }

    @Test
    fun getHourlyWeather_forceUpdateTrue_remoteData() = runTest{
        //Given Created Repository

        //When
        val data = repository.getHourlyWeather(LatLng(20.0,10.0),"", "en", true)

        //Assert that data is not empty and is equal to remote Data
        assertThat(data.first(), `is`(notNullValue()))
        assertThat(data.first().size, `is`(remoteHourlyList.size))
        assertThat(data.first(), `is`(remoteHourlyList))
    }
    @Test
    fun getHourlyWeather_forceUpdateFalse_localData() = mainCoroutineRule.runBlockingTest{
        //Given Created Repository with saved data in local data source

        //When
        val data = repository.getHourlyWeather(LatLng(20.0,10.0),"", "en", false)

        //Assert that data is not empty and is equal to local Data
        assertThat(data.first(), `is`(notNullValue()))
        assertThat(data.first().size, `is`(localHourlyWeatherList.size))
        assertThat(data.first(), `is`(localHourlyWeatherList))
    }

    @Test
    fun getCurrentWeather_forceUpdateTrue_remoteData() = runTest{
        //Given Created Repository

        //When
        val data = repository.getCurrentWeather(LatLng(20.0,10.0),"", "en", true)

        //Assert that data is not empty and is equal to remote Data
        assertThat(data.first(), `is`(notNullValue()))
        assertThat(data.first(), `is`(currentRemoteWeather))
    }
    @Test
    fun getCurrentWeather_forceUpdateFalse_localData() = runTest{
        //Given Created Repository

        //When
        val data = repository.getCurrentWeather(LatLng(20.0,10.0),"", "en", false)

        //Assert that data is not empty and is equal to remote Data
        assertThat(data.first(), `is`(notNullValue()))
        assertThat(data.first(), `is`(localCurrentWeather))
    }

    @Test
    fun updateWeatherCache_getLocalWeather_remoteData() = runTest{
        //Given
        val latLng = LatLng(20.0, 3.0)
        repository.updateWeatherCache(latLng, "")

        //When get Weather with force update false
        val dailyWeather = repository.getDailyWeather(latLng, "", "en", false)
        val hourlyWeather = repository.getHourlyWeather(latLng, "", "en", false)
        val currentWeather = repository.getCurrentWeather(latLng, "", "en", false)

        //Assert That data is Remote Data
        assertThat(dailyWeather.first(), `is`(notNullValue()))
        assertThat(dailyWeather.first().size, `is`(remoteDailyList.size))
        assertThat(dailyWeather.first().take(remoteDailyList.size), `is`(remoteDailyList))

        assertThat(hourlyWeather.first(), `is`(notNullValue()))
        assertThat(hourlyWeather.first().size, `is`(remoteHourlyList.size))
        assertThat(hourlyWeather.first().take(remoteDailyList.size), `is`(remoteHourlyList))

        assertThat(currentWeather.first(), `is`(notNullValue()))
        assertThat(currentWeather.first(), `is`(currentRemoteWeather))
    }

    @Test
    fun getSearchSuggestions_getsSuggestion() = runTest{
        //Given
        val query = "San"

        //When
        val result = repository.getSearchSuggestions(query, "", "", 5)

        //Assert That result is correct
        assertThat(result.first(), `is`(notNullValue()))
        result.first().forEach {
            assertThat(it.name?.contains(query), `is`(true))
        }
    }

}