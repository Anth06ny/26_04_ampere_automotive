package com.amonteiro.a26_04_ampere_automotive.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

suspend fun main() {

    val list = WeatherAPI.loadWeathers("toulouse")

    list.forEach {
        println(it.getResume())
    }

}

object WeatherAPI {
    //Déclaration du client
    private val client = HttpClient {
        install(Logging) {
            //(import io.ktor.client.plugins.logging.Logger)
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
            level = LogLevel.INFO  // TRACE, HEADERS, BODY, etc.
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }

    suspend fun loadWeathers(cityName: String): List<WeatherEntity> {

        val response = client.get("https://api.openweathermap.org/data/2.5/find?q=$cityName&appid=b80967f0a6bd10d23e44848547b26550&units=metric&lang=fr")
        if (!response.status.isSuccess()) {
            throw Exception("Erreur API: ${response.status} - ${response.bodyAsText()}")
        }
        val list = response.body<WeatherAPIResult>().list

        list.forEach{
            it.weather.forEach {
                it.icon = "https://openweathermap.org/img/wn/${it.icon}@4x.png"
            }
        }

        return list
    }

    fun close() {
        client.close()
    }
}

/* -------------------------------- */
// WEATHER
/* -------------------------------- */
@Serializable
data class WeatherAPIResult(val list: List<WeatherEntity>)

@Serializable
data class WeatherEntity(
    val id: Int, val name: String, var main: TempEntity,
    var weather: List<DescriptionEntity>,
    var wind: WindEntity
) {

    fun getResume() = """
            Il fait ${main.temp}° à $name (id=$id) avec un vent de ${wind.speed} m/s
            -Description : ${weather.firstOrNull()?.description ?: "-"}
            -Icône : ${weather.firstOrNull()?.icon ?: "-"}
        """.trimIndent()

}

@Serializable
data class TempEntity(var temp: Double)

@Serializable
data class DescriptionEntity(var description: String, var icon: String)

@Serializable
data class WindEntity(var speed: Double)