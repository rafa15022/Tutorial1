package dam_A15022.coolweatherapp.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object WeatherApiClient {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun getWeather(
        lat: Float,
        lon: Float
    ): WeatherData? {

        val requestUrl = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=$lat&longitude=$lon&")
            append("current_weather=true&")
            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m&")
            append("daily=sunrise,sunset&")
            append("timezone=auto")
        }

        return try {
            client.get(requestUrl).body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}