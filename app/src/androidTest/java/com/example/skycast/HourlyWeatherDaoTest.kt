package com.example.skycast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.skycast.home.model.db.WeatherDB
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


}