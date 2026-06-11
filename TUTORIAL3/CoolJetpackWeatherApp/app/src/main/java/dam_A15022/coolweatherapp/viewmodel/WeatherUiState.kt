package dam_A15022.coolweatherapp.viewmodel

data class WeatherUiState(
    val latitude: Float = 38.076f,
    val longitude: Float = -9.12f,
    val temperature: Float = 0f,
    val windspeed: Float = 0f,
    val winddirection: Int = 0,
    val weathercode: Int = 0,
    val seaLevelPressure: Float = 0f,
    val time: String = "",
    val isDay: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)