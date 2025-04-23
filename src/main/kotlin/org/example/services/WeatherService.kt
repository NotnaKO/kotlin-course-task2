package org.example.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.*
import kotlinx.serialization.json.Json

import org.example.models.TemperatureAnalysis
import org.example.models.TemperatureData
import org.example.models.WeatherResponse
import org.example.ui.Location

class WeatherService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    private val cache = mutableMapOf<String, WeatherResponse>()

    suspend fun getHistoricalWeatherData(
        location: Location,
        dateRange: ClosedRange<LocalDate>,
    ): WeatherResponse {
        val cacheKey = "$location-$dateRange"

        // Check cache first
        if (cache.containsKey(cacheKey)) {
            println("Using cached data")
            return cache[cacheKey]!!
        }

        val url = "https://api.open-meteo.com/v1/forecast"

        try {
            val response: WeatherResponse = client.get(url) {
                parameter("latitude", location.latitude)
                parameter("longitude", location.longitude)
                parameter("hourly", "temperature_2m")
                parameter("start_date", dateRange.start)
                parameter("end_date", dateRange.endInclusive)
            }.apply {
                if (!status.isSuccess()) {
                    throw ResponseException(this, "Failed to fetch data from Open-Meteo API")
                }
            }.body()

            // Cache the response
            cache[cacheKey] = response

            return response
        } catch (e: ResponseException) {
            return WeatherResponse(
                latitude = location.latitude,
                longitude = location.longitude,
                error = true,
                reason = e.message
            )
        }
    }

    fun close() {
        client.close()
    }
}

fun analyzeTemperatureData(response: WeatherResponse): TemperatureAnalysis? {
    if (response.error || response.hourly == null) {
        return null
    }

    val times = response.hourly.time
    val temperatures = response.hourly.temperature_2m

    if (times.isEmpty() || temperatures.isEmpty()) {
        return null
    }

    val avgTemp = temperatures.average()
    val maxTempIndex = temperatures.indices.maxBy { temperatures[it] }
    val minTempIndex = temperatures.indices.minBy { temperatures[it] }

    val maxTemp = temperatures[maxTempIndex]
    val maxTempTime = times[maxTempIndex]
    val minTempTime = times[minTempIndex]
    val minTemp = temperatures[minTempIndex]

    return TemperatureAnalysis(
        maxTemperature = TemperatureData(maxTempTime, maxTemp),
        minTemperature = TemperatureData(minTempTime, minTemp),
        averageTemperature = avgTemp
    )
}