package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: HourlyData? = null,
    val hourlyUnits: HourlyUnits? = null,
    val error: Boolean = false,
    val reason: String? = null
)

@Serializable
data class HourlyData(
    val time: List<String>,
    val temperature_2m: List<Double>
)

@Serializable
data class HourlyUnits(
    val temperature_2m: String
)

data class TemperatureData(
    val time: String,
    val temperature: Double
)

data class TemperatureAnalysis(
    val maxTemperature: TemperatureData,
    val minTemperature: TemperatureData,
    val averageTemperature: Double
)