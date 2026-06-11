package dam_A15022.coolweatherapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dam_A15022.coolweatherapp.R

@Composable
fun WeatherCard(
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    weatherCode: Int,
    seaLevelPressure: Float,
    time: String
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            WeatherRow(
                label = stringResource(R.string.temperature),
                value = "$temperature °C"
            )

            WeatherRow(
                label = stringResource(R.string.wind_speed),
                value = "$windSpeed km/h"
            )

            WeatherRow(
                label = stringResource(R.string.wind_direction),
                value = "$windDirection°"
            )

            WeatherRow(
                label = stringResource(R.string.weather_code),
                value = weatherCode.toString()
            )

            WeatherRow(
                label = stringResource(R.string.sea_level_pressure),
                value = "$seaLevelPressure hPa"
            )

            WeatherRow(
                label = stringResource(R.string.time),
                value = time
            )
        }
    }
}