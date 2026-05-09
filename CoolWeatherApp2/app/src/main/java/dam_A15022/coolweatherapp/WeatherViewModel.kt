package dam_A15022.coolweatherapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel : ViewModel() {
    val weatherLiveData = MutableLiveData<WeatherData>()

    private val repository = WeatherRepository()

    fun fetchWeather(lat: Float, lon: Float) {
        Thread {
            val weather = repository.getWeather(lat, lon)
            weatherLiveData.postValue(weather)
        }.start()
    }
}