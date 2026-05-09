package dam_A15022.coolweatherapp

import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

class WeatherRepository {

    fun getWeather(lat: Float, lon: Float): WeatherData {
        val reqString = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=$lat&longitude=$lon&")
            append("current_weather=true&")
            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m&")
            append("daily=sunrise,sunset&")
            append("timezone=auto")
        }

        val url = URL(reqString)

        url.openStream().use {
            return Gson().fromJson(
                InputStreamReader(it, "UTF-8"),
                WeatherData::class.java
            )
        }
    }
}