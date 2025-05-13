package org.example

import kotlinx.coroutines.runBlocking
import org.example.services.WeatherService
import org.example.services.analyzeTemperatureData
import org.example.ui.displayError
import org.example.ui.displayResults
import org.example.ui.getDateRangeInput
import org.example.ui.getLocationInput

fun main() = runBlocking {
    val weatherService = WeatherService()

    println("Welcome to Weather Analysis Tool")
    println("This application uses the Open-Meteo API to analyze weather data")

    try {
        while (true) {
            println("Would you like to make a new request? (yes to continue, no to exit)")
            val userChoice = readlnOrNull()?.trim()?.lowercase()
            if (userChoice == "no") {
                println("Exiting the application. Goodbye!")
                break
            }
            if (userChoice != "yes") {
                println("Invalid input. Please type 'yes' to continue or 'no' to exit.")
                continue
            }
            val location = getLocationInput()

            // Get date range input
            val dateRange = getDateRangeInput()

            println("\nFetching weather data. Please wait...")

            // Get weather data
            val response = weatherService.getHistoricalWeatherData(
                location, dateRange,
            )

            if (response.error) {
                displayError("Failed to fetch weather data: ${response.reason ?: "Unknown error"}")
            } else {
                // Analyze data
                val analysis = analyzeTemperatureData(response)

                // Display results
                displayResults(analysis, location)
            }
        }
    } catch (e: Exception) {
        displayError("An unexpected error occurred: ${e.message}")
    } finally {
        weatherService.close()
    }
}