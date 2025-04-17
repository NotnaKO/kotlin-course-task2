package org.example.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.*
import kotlinx.serialization.json.Json

import org.example.models.TemperatureAnalysis
import org.example.models.TemperatureData
import org.example.models.WeatherResponse

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
        latitude: Double,
        longitude: Double,
        startDate: LocalDate,
        endDate: LocalDate
    ): WeatherResponse {
        val cacheKey = "$latitude-$longitude-$startDate-$endDate"

        // Check cache first
        if (cache.containsKey(cacheKey)) {
            println("Using cached data")
            return cache[cacheKey]!!
        }

        val url = "https://api.open-meteo.com/v1/forecast"

        try {
            val response: WeatherResponse = client.get(url) {
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("hourly", "temperature_2m")
                parameter("start_date", startDate.toString())
                parameter("end_date", endDate.toString())
            }.apply {
                if (status != HttpStatusCode.OK) {
                    throw Exception("Failed to fetch data: $status \n${body<String>()}")
                }
            }.body()

            // Cache the response
            cache[cacheKey] = response

            return response
        } catch (e: Exception) {
            println("Error fetching weather data: ${e.message}")
            return WeatherResponse(
                latitude = latitude,
                longitude = longitude,
                error = true,
                reason = e.message
            )
        }
    }

    fun analyzeTemperatureData(response: WeatherResponse): TemperatureAnalysis {
        if (response.error || response.hourly == null) {
            return TemperatureAnalysis(null, null, null)
        }

        val times = response.hourly.time
        val temperatures = response.hourly.temperature_2m

        if (times.isEmpty() || temperatures.isEmpty()) {
            return TemperatureAnalysis(null, null, null)
        }

        var maxTemp = temperatures[0]
        var minTemp = temperatures[0]
        var maxTempTime = times[0]
        var minTempTime = times[0]
        var sum = 0.0

        for (i in temperatures.indices) {
            val temp = temperatures[i]

            if (temp > maxTemp) {
                maxTemp = temp
                maxTempTime = times[i]
            }

            if (temp < minTemp) {
                minTemp = temp
                minTempTime = times[i]
            }

            sum += temp
        }

        val avgTemp = sum / temperatures.size

        return TemperatureAnalysis(
            maxTemperature = TemperatureData(maxTempTime, maxTemp),
            minTemperature = TemperatureData(minTempTime, minTemp),
            averageTemperature = avgTemp
        )
    }

    fun close() {
        client.close()
    }
}