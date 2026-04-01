package com.amonteiro.a26_04_ampere_automotive

import com.amonteiro.a26_04_ampere_automotive.data.remote.DescriptionEntity
import com.amonteiro.a26_04_ampere_automotive.data.remote.TempEntity
import com.amonteiro.a26_04_ampere_automotive.data.remote.WeatherEntity
import com.amonteiro.a26_04_ampere_automotive.data.remote.WindEntity
import com.amonteiro.a26_04_ampere_automotive.ui.MainViewModel
import kotlinx.coroutines.Job

class MainViewModelTest : MainViewModel() {

    companion object {
        const val ERROR_MESSAGE_TEST = "Une erreur est survenue"
    }

    //Méthode pour définir un état particulier à tester
    fun errorState() {
        runInProgress.value = false
        errorMessage.value = ERROR_MESSAGE_TEST
        dataList.value = emptyList()
    }

    //On peut surcharger les méthodes pour tester leurs appels
    override fun loadWeathers(cityName: String): Job? {
        runInProgress.value = false
        errorMessage.value = ""
        dataList.value = fakeList(cityName)

        return null
    }

    fun loadingState() {
        runInProgress.value = true
        errorMessage.value = ""
        dataList.value = emptyList()
    }

    fun successState() {
        runInProgress.value = false
        errorMessage.value = ""
        dataList.value = fakeList()
    }

    fun fakeList(cityName :String = "Nice") = listOf(
        WeatherEntity(
            id = 1,
            name = "$cityName 1",
            main = TempEntity(temp = 18.5),
            weather = listOf(
                DescriptionEntity(description = "ciel dégagé", icon = "01d")
            ),
            wind = WindEntity(speed = 5.0)
        ),
        WeatherEntity(
            id = 2,
            name = "$cityName 2",
            main = TempEntity(temp = 22.3),
            weather = listOf(
                DescriptionEntity(description = "partiellement nuageux", icon = "02d")
            ),
            wind = WindEntity(speed = 3.2)
        )
    )
}