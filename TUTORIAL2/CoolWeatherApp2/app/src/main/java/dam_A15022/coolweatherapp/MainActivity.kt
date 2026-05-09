package dam_A15022.coolweatherapp

import androidx.lifecycle.ViewModelProvider
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.ProgressBar
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: WeatherViewModel
    private val LOCATION_PERM = 1001
    private var day = false

    private fun isDay(weather: WeatherData): Boolean {
        val nowHour = weather.current_weather.time.substring(11, 13).toInt()
        val sunriseHour = weather.daily.sunrise[0].substring(11, 13).toInt()
        val sunsetHour = weather.daily.sunset[0].substring(11, 13).toInt()

        return nowHour >= sunriseHour && nowHour < sunsetHour
    }

    private fun recreateWithTheme(newDay: Boolean) {
        val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
        val savedDay = prefs.getBoolean("day", false)

        if (newDay != savedDay) {
            prefs.edit().putBoolean("day", newDay).apply()
            recreate()
        }
    }

    private fun applyTheme() {
        val isTablet = resources.configuration.smallestScreenWidthDp >= 600
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                if (isTablet) {
                    if (day) setTheme(R.style.Theme_Day_Tablet) else setTheme(R.style.Theme_Night_Tablet)
                } else {
                    if (day) setTheme(R.style.Theme_Day) else setTheme(R.style.Theme_Night)
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                if (isTablet) {
                    if (day) setTheme(R.style.Theme_Day_Tablet_Land) else setTheme(R.style.Theme_Night_Tablet_Land)
                } else {
                    if (day) setTheme(R.style.Theme_Day_Land) else setTheme(R.style.Theme_Night_Land)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        day = getSharedPreferences("weather_prefs", MODE_PRIVATE)
            .getBoolean("day", false)

        applyTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        viewModel.weatherLiveData.observe(this) { weather ->
            updateUI(weather)
        }

        requestLocationPermission()

        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val editLat = findViewById<EditText>(R.id.editLat)
        val editLon = findViewById<EditText>(R.id.editLon)

        btnUpdate.setOnClickListener {
            val lat = editLat.text.toString().toFloatOrNull() ?: 38.76f
            val lon = editLon.text.toString().toFloatOrNull() ?: -9.12f

            showLoading()
            viewModel.fetchWeather(lat, lon)
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERM
            )
        } else {
            getGpsLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (
            requestCode == LOCATION_PERM &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getGpsLocation()
        } else {
            viewModel.fetchWeather(38.076f, -9.12f)
        }
    }

    private fun getGpsLocation() {
        val locationManager =
            getSystemService(LOCATION_SERVICE) as android.location.LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.fetchWeather(38.076f, -9.12f)
            return
        }

        val location =
            locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)

        if (location != null) {
            viewModel.fetchWeather(
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )
        } else {
            viewModel.fetchWeather(38.076f, -9.12f)
        }
    }

    private fun showLoading() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE
    }
    private fun updateUI(request: WeatherData) {
        val newDay = isDay(request)

        runOnUiThread {
            val weatherImage = findViewById<ImageView>(R.id.weatherImage)
            val pressure = findViewById<TextView>(R.id.pressureValue)
            val windDir = findViewById<TextView>(R.id.windDirValue)
            val windSpeed = findViewById<TextView>(R.id.windSpeedValue)
            val temp = findViewById<TextView>(R.id.tempValue)
            val time = findViewById<TextView>(R.id.timeValue)
            val editLat = findViewById<EditText>(R.id.editLat)
            val editLon = findViewById<EditText>(R.id.editLon)

            editLat.setText(request.latitude)
            editLon.setText(request.longitude)

            pressure.text = request.hourly.pressure_msl.get(12).toString() + " hPa"
            windDir.text = request.current_weather.winddirection.toString() + "°"
            windSpeed.text = request.current_weather.windspeed.toString() + " km/h"
            temp.text = request.current_weather.temperature.toString() + " ºC"
            time.text = request.current_weather.time

            val weatherMap = getWeatherCodeMap()
            val wCode = weatherMap[request.current_weather.weathercode]

            val wImage = when (wCode) {
                WMO_WeatherCode.CLEAR_SKY,
                WMO_WeatherCode.MAINLY_CLEAR,
                WMO_WeatherCode.PARTLY_CLOUDY ->
                    if (newDay) "${wCode.image}day" else "${wCode.image}night"
                else -> wCode?.image
            }

            val resID = resources.getIdentifier(wImage, "drawable", packageName)
            if (resID != 0) {
                weatherImage.setImageResource(resID)
            }

            hideLoading()
            recreateWithTheme(newDay)
        }
    }
}