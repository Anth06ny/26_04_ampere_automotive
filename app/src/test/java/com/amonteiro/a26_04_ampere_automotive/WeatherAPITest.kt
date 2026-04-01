package com.amonteiro.a26_04_ampere_automotive

import com.amonteiro.a26_04_ampere_automotive.data.remote.WeatherAPI
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Test


class KtorWeatherAPITest {

    @Test
    fun loadWeatherNiceTest() = runBlocking<Unit> {

        var result = WeatherAPI.loadWeathers("Nice")

        assertTrue("La liste est vide", result.isNotEmpty())

        for (city in result) {
            assertTrue("Le nom ne contient pas Nice", city.name.contains("Nice", true))
            assertTrue("La température n'est pas entre -40 et 60°", city.main.temp in -40.0..60.0)
            assertTrue("La description est vide", city.weather.isNotEmpty())
            assertTrue("Il n'y a pas d'icône", city.weather[0].icon.isNotBlank())
        }
    }

    @Test
    fun loadWeathersEmptyString()  = runBlocking<Unit> {
        try {
            WeatherAPI.loadWeathers("")
            fail("L'appel avec une ville vide aurait du lever une exception")
        } catch (e: Exception) {
            //ok
        }
    }

    @Test(expected = Exception::class)
    fun loadWeathersEmptyString2()  = runBlocking<Unit> {
        WeatherAPI.loadWeathers("")
    }

    //Nécessite de mettre en place JUnit5 (par défault le 4)
    //Dans libs.versions.toml
    //@Test
    //fun loadWeathersEmptyString3() = runBlocking<Unit> {
    //    assertThrows<Exception> {
    //        KtorWeatherAPI.loadWeathers("")
    //    }
    //}

}