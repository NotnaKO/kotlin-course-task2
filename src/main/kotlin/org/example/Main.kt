package org.example

import kotlinx.coroutines.runBlocking
import org.example.services.WeatherService
import org.example.ui.ConsoleUI

fun main() = runBlocking {
    val weatherService = WeatherService()
    val ui = ConsoleUI()

    println("Welcome to Weather Analysis Tool")
    println("This application uses the Open-Meteo API to analyze weather data")

    try {
        val (latitude, longitude) = ui.getLocationInput()

        // Get date range input
        val (startDate, endDate) = ui.getDateRangeInput()

        println("\nFetching weather data. Please wait...")

        // Get weather data
        val response = weatherService.getHistoricalWeatherData(
            latitude, longitude, startDate, endDate
        )

        if (response.error) {
            ui.displayError("Failed to fetch weather data: ${response.reason ?: "Unknown error"}")
        } else {
            // Analyze data
            val analysis = weatherService.analyzeTemperatureData(response)

            // Display results
            ui.displayResults(analysis, latitude, longitude)
        }
    } catch (e: Exception) {
        ui.displayError("An unexpected error occurred: ${e.message}")
    } finally {
        weatherService.close()
    }
}