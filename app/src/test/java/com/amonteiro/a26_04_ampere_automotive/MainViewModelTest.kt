package com.amonteiro.a26_04_ampere_automotive

import com.amonteiro.a26_04_ampere_automotive.data.remote.DescriptionEntity
import com.amonteiro.a26_04_ampere_automotive.data.remote.TempEntity
import com.amonteiro.a26_04_ampere_automotive.data.remote.WeatherAPI
import com.amonteiro.a26_04_ampere_automotive.data.remote.WeatherEntity
import com.amonteiro.a26_04_ampere_automotive.data.remote.WindEntity
import com.amonteiro.a26_04_ampere_automotive.ui.MainViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainViewModelTest {

    //Pour piloter les coroutines
    private val testDispatcher = StandardTestDispatcher()


    @Test
    fun loadWeathersNetwork() = runBlocking {

        val viewModel = MainViewModel()

        // Au départ => false
        assertEquals(false, viewModel.runInProgress.value)

        // Lancement de loadWeathers
        val job = viewModel.loadWeathers("Paris")

        // Immédiatement après appel (avant avancer la coroutine)
        assertEquals(true, viewModel.runInProgress.value)

        //attendre la fin de la tache asynchrone
        job.join()

        // Après exécution => false
        assertEquals(false, viewModel.runInProgress.value)

        //Contient au moins 1 élément
        assertTrue("La ville n'est pas Paris",  viewModel.dataList.value.first().name.contains("Paris", true))
    }

    @Test
    fun loadWeathersMock() = runTest(testDispatcher) {

        val viewModel = MainViewModel(testDispatcher)

        // Vérifier l'état avant le lancement de la coroutine
        // Au départ => false
        assertEquals(false, viewModel.runInProgress.value)

        //On mock KtorWeatherAPI pour déclencher le résultat voulu
        mockkObject(WeatherAPI)
        coEvery { WeatherAPI.loadWeathers("Paris") }.returns(getParisFakeResult())

        // Appeler la méthode à tester
        viewModel.loadWeathers("Paris")

        // Vérifier que runInProgress est true
        assertTrue(viewModel.runInProgress.value)

        //job.join()
        advanceUntilIdle()

        // Vérifier que runInProgress est false
        assertFalse(viewModel.runInProgress.value)

        //On vérifie que loadWeathers("Paris") à bien été appelé
        coVerify { WeatherAPI.loadWeathers("Paris") }

        //On vérifie qu'aucun autre appel à KtorWeatherAPI à été effectué
        confirmVerified(WeatherAPI)

        // Qu'on a des éléments dans la liste
        assertFalse(viewModel.dataList.value.isEmpty())

        //Que le 1 er élément c'est bien Paris et le même id
        assertEquals("La ville n'est pas Paris", getParisFakeResult().first().name, viewModel.dataList.value.first().name)
        assertEquals("L'id n'est pas identique", getParisFakeResult().first().id, viewModel.dataList.value.first().id)

    }

    fun getParisFakeResult() = arrayListOf(
        WeatherEntity(
            id = 1,
            name = "Paris",
            main = TempEntity(temp = 20.0),
            wind = WindEntity(speed = 5.0),
            weather = listOf(DescriptionEntity(description = "Ensoleillé", icon = "01d"))
        )
    )
}