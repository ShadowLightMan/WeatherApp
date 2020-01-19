package com.weather.weatherapp.ui.home

import com.weather.weatherapp.domain.models.UiWeatherData
import com.weather.weatherapp.data.models.Config
import com.weather.weatherapp.data.models.Weather
import com.weather.weatherapp.data.models.WeatherData
import com.weather.weatherapp.data.models.Wind
import com.weather.weatherapp.utils.*
import com.weather.weatherapp.utils.Constants.CELSIUS
import com.weather.weatherapp.utils.Constants.FAHRENHEIT
import com.weather.weatherapp.utils.Constants.HPA
import com.weather.weatherapp.utils.Constants.KM
import com.weather.weatherapp.utils.Constants.MBAR
import com.weather.weatherapp.utils.Constants.METERS
import com.weather.weatherapp.utils.Constants.MILES
import com.weather.weatherapp.utils.Constants.MMHG
import kotlin.math.roundToInt

class WeatherDataMapper(
    private val resourceManager: ResourceManager,
    private val metricsConverter: MetricsConverter,
    private val config: Config
) {

    fun toUiModel(weatherData: WeatherData): UiWeatherData {

        val weather = weatherData.weather?.get(0) ?: Weather()
        weather.icon = "http://openweathermap.org/img/wn/${weather.icon}@2x.png"
        weather.description = weather.description.capitalize()

        return UiWeatherData(
            temp = convertTemp(weatherData.main.temp),
            feelsLike = convertTemp(weatherData.main.feelsLike),
            weather = weather,
            wind = convertWind(weatherData.wind),
            pressure = convertPressure(weatherData.main.pressure),
            visibility = convertVisibility(weatherData.visibility),
            name = weatherData.name,
            humidity = "${weatherData.main.humidity} %"
        )
    }

    private fun convertPressure(pressure: Int): String{
        return when(config.pressureMetric){
            MBAR -> "${metricsConverter.toMbar(Hpa(pressure))} ${resourceManager.pressureMbar()}"
            MMHG -> "${metricsConverter.toMmhg(Hpa(pressure))} ${resourceManager.pressureMmhg()}"
            HPA -> "$pressure ${resourceManager.pressureHpa()}"
            else -> ""
        }
    }

    private fun convertVisibility(visibility: Double): String {
        return when (config.visibilityMetric) {
            KM -> "${metricsConverter.toKm(Meters(visibility))} ${resourceManager.visibilityKm()}"
            MILES -> "${metricsConverter.toMiles(Meters(visibility))} ${resourceManager.visibilityMiles()}"
            else -> ""
        }
    }

    private fun convertTemp(temp: Double): String {
        val tempData = when (config.tempMetric) {
            FAHRENHEIT -> metricsConverter.toFahrenheit(Celsius(temp))
            CELSIUS -> temp.roundToInt()
            else -> 0
        }
        return when {
            tempData > 0 -> "+$tempData °"
            else -> "$tempData °"
        }
    }

    private fun convertWind(wind: Wind): String {
        return when (config.windMetric) {
            KM -> "${wind.speed.roundToInt()} ${resourceManager.speedKmPerHour()}"
            MILES -> "${metricsConverter.toMiles(KiloMeters(wind.speed))} ${resourceManager.speedMilesPerHour()}"
            METERS -> "${metricsConverter.toMetersPerSecond(KiloMeters(wind.speed))} ${resourceManager.speedMetersPerSecond()}"
            else -> ""
        } + " ${toTextualDescription(wind.deg)}"
    }

    private fun toTextualDescription(degree: Int): String {
        if (degree > 337.5) return resourceManager.northerly()
        if (degree > 292.5) return resourceManager.northWesterly()
        if (degree > 247.5) return resourceManager.westerly()
        if (degree > 202.5) return resourceManager.southWesterly()
        if (degree > 157.5) return resourceManager.southerly()
        if (degree > 122.5) return resourceManager.southEasterly()
        if (degree > 67.5) return resourceManager.easterly()
        if (degree > 22.5) return resourceManager.northEasterly()
        return resourceManager.northerly()
    }
}