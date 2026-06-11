package dam_A15022.coolweatherapp.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dam_A15022.coolweatherapp.R
import dam_A15022.coolweatherapp.data.WMO_WeatherCode
import dam_A15022.coolweatherapp.data.getWeatherCodeMap
import dam_A15022.coolweatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherUI(
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()

    var latitudeText by rememberSaveable {
        mutableStateOf(weatherState.latitude.toString())
    }

    var longitudeText by rememberSaveable {
        mutableStateOf(weatherState.longitude.toString())
    }

    LaunchedEffect(Unit) {
        weatherViewModel.fetchWeather()
    }

    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    val currentWeatherCode =
        getWeatherCodeMap()[weatherState.weathercode]

    val imageName = when (currentWeatherCode) {
        WMO_WeatherCode.CLEAR_SKY,
        WMO_WeatherCode.MAINLY_CLEAR,
        WMO_WeatherCode.PARTLY_CLOUDY -> {
            if (weatherState.isDay) {
                currentWeatherCode?.image + "day"
            } else {
                currentWeatherCode?.image + "night"
            }
        }

        else -> currentWeatherCode?.image
    }

    val weatherIcon = context.resources.getIdentifier(
        imageName,
        "drawable",
        context.packageName
    )

    val onLatitudeChange: (String) -> Unit = { value ->
        latitudeText = value

        value.toFloatOrNull()?.let {
            weatherViewModel.updateLatitude(it)
        }
    }

    val onLongitudeChange: (String) -> Unit = { value ->
        longitudeText = value

        value.toFloatOrNull()?.let {
            weatherViewModel.updateLongitude(it)
        }
    }

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        LandscapeWeatherUI(
            weatherIcon = weatherIcon,
            latitudeText = latitudeText,
            longitudeText = longitudeText,
            temperature = weatherState.temperature,
            windSpeed = weatherState.windspeed,
            windDirection = weatherState.winddirection,
            weatherCode = weatherState.weathercode,
            seaLevelPressure = weatherState.seaLevelPressure,
            time = weatherState.time,
            isDay = weatherState.isDay,
            isLoading = weatherState.isLoading,
            errorMessage = weatherState.errorMessage,
            onLatitudeChange = onLatitudeChange,
            onLongitudeChange = onLongitudeChange,
            onUpdateButtonClick = {
                weatherViewModel.fetchWeather()
            }
        )
    } else {
        PortraitWeatherUI(
            weatherIcon = weatherIcon,
            latitudeText = latitudeText,
            longitudeText = longitudeText,
            temperature = weatherState.temperature,
            windSpeed = weatherState.windspeed,
            windDirection = weatherState.winddirection,
            weatherCode = weatherState.weathercode,
            seaLevelPressure = weatherState.seaLevelPressure,
            time = weatherState.time,
            isDay = weatherState.isDay,
            isLoading = weatherState.isLoading,
            errorMessage = weatherState.errorMessage,
            onLatitudeChange = onLatitudeChange,
            onLongitudeChange = onLongitudeChange,
            onUpdateButtonClick = {
                weatherViewModel.fetchWeather()
            }
        )
    }
}

@Composable
fun PortraitWeatherUI(
    weatherIcon: Int,
    latitudeText: String,
    longitudeText: String,
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    weatherCode: Int,
    seaLevelPressure: Float,
    time: String,
    isDay: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit
) {
    val backgroundImage = if (isDay) {
        R.drawable.fundao
    } else {
        R.drawable.fundaonoite
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(backgroundImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 42.dp,
                    bottom = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_title),
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (weatherIcon != 0) {
                Image(
                    painter = painterResource(weatherIcon),
                    contentDescription = stringResource(R.string.weather_icon),
                    modifier = Modifier.size(105.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            CoordinatesCard(
                latitudeText = latitudeText,
                longitudeText = longitudeText,
                onLatitudeChange = onLatitudeChange,
                onLongitudeChange = onLongitudeChange,
                onUpdateButtonClick = onUpdateButtonClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White
                )
            } else {
                WeatherCard(
                    temperature = temperature,
                    windSpeed = windSpeed,
                    windDirection = windDirection,
                    weatherCode = weatherCode,
                    seaLevelPressure = seaLevelPressure,
                    time = time
                )
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun LandscapeWeatherUI(
    weatherIcon: Int,
    latitudeText: String,
    longitudeText: String,
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    weatherCode: Int,
    seaLevelPressure: Float,
    time: String,
    isDay: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit
) {
    val backgroundImage = if (isDay) {
        R.drawable.fundaodeitado
    } else {
        R.drawable.fundaonoitedeitado
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(backgroundImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_title),
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (weatherIcon != 0) {
                    Image(
                        painter = painterResource(weatherIcon),
                        contentDescription = stringResource(R.string.weather_icon),
                        modifier = Modifier
                            .size(125.dp)
                            .weight(0.7f)
                    )
                }

                Column(
                    modifier = Modifier.weight(1.2f)
                ) {
                    CoordinatesCard(
                        latitudeText = latitudeText,
                        longitudeText = longitudeText,
                        onLatitudeChange = onLatitudeChange,
                        onLongitudeChange = onLongitudeChange,
                        onUpdateButtonClick = onUpdateButtonClick
                    )
                }

                Column(
                    modifier = Modifier.weight(1.2f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    } else {
                        WeatherCard(
                            temperature = temperature,
                            windSpeed = windSpeed,
                            windDirection = windDirection,
                            weatherCode = weatherCode,
                            seaLevelPressure = seaLevelPressure,
                            time = time
                        )
                    }
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}