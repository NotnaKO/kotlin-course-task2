package org.example

import kotlinx.coroutines.runBlocking
import org.example.services.WeatherService
import org.example.services.analyzeTemperatureData
import org.example.ui.ConsoleUI

fun main() = runBlocking {
    val weatherService = WeatherService()
    val ui = ConsoleUI()

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
                val analysis = analyzeTemperatureData(response)

                // Display results
                ui.displayResults(analysis, latitude, longitude)
            }
        }
    } catch (e: Exception) {
        ui.displayError("An unexpected error occurred: ${e.message}")
    } finally {
        weatherService.close()
    }
}