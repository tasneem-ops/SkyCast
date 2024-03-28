package com.example.skycast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.skycast.favorites.model.dto.FavDTO
import com.example.skycast.favorites.viewmodel.FavoritesViewModel
import com.example.skycast.model.Response
import com.example.skycast.model.repository.FakeWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.isA
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    @get:Rule
    val instantTask = InstantTaskExecutorRule()

    lateinit var favoritesViewModel: FavoritesViewModel
    val fav1 = FavDTO("Alex", 30.0, 20.0)
    val fav2 = FavDTO("Ismailia", 35.0, 10.0)
    @Before
    fun createWeatherViewModel(){
        val fakeWeatherRepository = FakeWeatherRepository()
        favoritesViewModel = FavoritesViewModel(fakeWeatherRepository, Dispatchers.Main)
    }

    @Test
    fun addFav_getAllFav_favItemAdded() = mainCoroutineRule.runBlockingTest{
        //Given Created ViewModel
        favoritesViewModel.addFav(fav1)

        //When getItems
        favoritesViewModel.getAllFav()
        val response = favoritesViewModel.respnseDataState

        //Assert That fav1 is the only item in list
        assertThat(response.value, isA(Response.Success::class.java))
        assertThat((response.value as Response.Success).data.size , `is`(1))
        assertThat((response.value as Response.Success).data.get(0) , `is`(fav1))
    }

    @Test
    fun deleteFav_getAllFav_favItemDeleted() = mainCoroutineRule.runBlockingTest{
        //Given Created ViewModel
        favoritesViewModel.addFav(fav1)
        favoritesViewModel.addFav(fav2)
        favoritesViewModel.deleteFav(fav1)

        //When getItems
        favoritesViewModel.getAllFav()
        val response = favoritesViewModel.respnseDataState

        //Assert That fav1 is the only item in list
        assertThat(response.value, isA(Response.Success::class.java))
        assertThat((response.value as Response.Success).data.size , `is`(1))
        assertThat((response.value as Response.Success).data.get(0) , `is`(fav2))
    }

}