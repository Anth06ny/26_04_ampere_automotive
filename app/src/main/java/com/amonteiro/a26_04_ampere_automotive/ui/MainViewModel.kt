package com.amonteiro.a26_04_ampere_automotive.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amonteiro.a26_04_ampere_automotive.data.remote.DescriptionEntity
import com.amonteiro.a26_04_ampere_automotive.data.remote.TempEntity
import com.amonteiro.a26_04_ampere_automotive.data.remote.WeatherAPI
import com.amonteiro.a26_04_ampere_automotive.data.remote.WeatherEntity
import com.amonteiro.a26_04_ampere_automotive.data.remote.WindEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher


fun main() {
    val mainViewModel = MainViewModel()
    println("res : " +  mainViewModel.dataList.value.size)
    mainViewModel.loadWeathers("Nice")

    while (mainViewModel.runInProgress.value) {
        println("Attente ...")
        Thread.sleep(500)
    }
    println("res : " +  mainViewModel.dataList.value.size)
}

open class MainViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {
    val dataList = MutableStateFlow(emptyList<WeatherEntity>())
    val runInProgress = MutableStateFlow(false)
    val errorMessage = MutableStateFlow("")

    init {//Création d'un jeu de donnée au démarrage
        println("Instanciation de MainViewModel")
        //loadFakeData()
    }

    fun loadFakeData(runInProgress :Boolean = false, errorMessage:String = "" ) {
        this.runInProgress.value = runInProgress
        this.errorMessage.value = errorMessage
        dataList.value = listOf(
            WeatherEntity(
                id = 1,
                name = "Paris",
                main = TempEntity(temp = 18.5),
                weather = listOf(
                    DescriptionEntity(description = "ciel dégagé", icon = "https://picsum.photos/200")
                ),
                wind = WindEntity(speed = 5.0)
            ),
            WeatherEntity(
                id = 2,
                name = "Toulouse",
                main = TempEntity(temp = 22.3),
                weather = listOf(
                    DescriptionEntity(description = "partiellement nuageux", icon = "https://picsum.photos/201")
                ),
                wind = WindEntity(speed = 3.2)
            ),
            WeatherEntity(
                id = 3,
                name = "Toulon",
                main = TempEntity(temp = 25.1),
                weather = listOf(
                    DescriptionEntity(description = "ensoleillé", icon = "https://picsum.photos/202")
                ),
                wind = WindEntity(speed = 6.7)
            ),
            WeatherEntity(
                id = 4,
                name = "Lyon",
                main = TempEntity(temp = 19.8),
                weather = listOf(
                    DescriptionEntity(description = "pluie légère", icon = "https://picsum.photos/203")
                ),
                wind = WindEntity(speed = 4.5)
            )
        ).shuffled() //shuffled() pour avoir un ordre différent à chaque appel
    }

    open fun loadWeathers(cityName: String): Job? {
        runInProgress.value = true
        errorMessage.value = ""
        //tache asynchrone
        val job = viewModelScope.launch(dispatcher) {
            try {
                dataList.value = WeatherAPI.loadWeathers(cityName)
            }
            //gestion des erreurs
            catch (e: Exception) {
                e.printStackTrace()
                errorMessage.value = e.message ?: "Une erreur est survenue"
            }
            runInProgress.value = false
        }

        return job
    }
}