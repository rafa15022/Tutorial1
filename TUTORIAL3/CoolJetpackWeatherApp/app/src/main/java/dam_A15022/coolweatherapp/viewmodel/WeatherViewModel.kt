package dam_A15022.coolweatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_A15022.coolweatherapp.data.WeatherApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun updateLatitude(latitude: Float) {
        _uiState.update {
            it.copy(latitude = latitude)
        }
    }

    fun updateLongitude(longitude: Float) {
        _uiState.update {
            it.copy(longitude = longitude)
        }
    }

    fun fetchWeather() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val currentState = _uiState.value

            val weather = WeatherApiClient.getWeather(
                currentState.latitude,
                currentState.longitude
            )

            if (weather != null) {
                val pressure = weather.hourly.pressure_msl
                    .getOrNull(12)
                    ?.toFloat()
                    ?: 0f

                val hour = weather.current_weather.time
                    .substringAfter("T")
                    .substringBefore(":")
                    .toIntOrNull()
                    ?: 12

                val isDay = hour in 6..18

                _uiState.update {
                    it.copy(
                        latitude = weather.latitude,
                        longitude = weather.longitude,
                        temperature = weather.current_weather.temperature,
                        windspeed = weather.current_weather.windspeed,
                        winddirection = weather.current_weather.winddirection,
                        weathercode = weather.current_weather.weathercode,
                        seaLevelPressure = pressure,
                        time = weather.current_weather.time,
                        isDay = isDay,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Não foi possível obter os dados meteorológicos."
                    )
                }
            }
        }
    }
}