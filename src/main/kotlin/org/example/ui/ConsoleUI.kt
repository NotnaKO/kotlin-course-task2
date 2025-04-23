package org.example.ui

import kotlinx.datetime.LocalDate
import org.example.models.TemperatureAnalysis
import java.time.format.DateTimeParseException

class ConsoleUI {
    fun getLocationInput(): Pair<Double, Double> {
        var latitude: Double? = null
        while (latitude == null) {
            print("Enter latitude (-90 to 90): ")
            try {
                val input = readlnOrNull()?.toDoubleOrNull()
                if (input != null && input in -90.0 .. 90.0) {
                    latitude = input
                } else {
                    println("Invalid latitude. Please enter a number between -90 and 90.")
                }
            } catch (e: Exception) {
                println("Invalid input. Please enter a valid number.")
            }
        }

        var longitude: Double? = null
        while (longitude == null) {
            print("Enter longitude (-180 to 180): ")
            try {
                val input = readlnOrNull()?.toDoubleOrNull()
                if (input != null && input >= -180 && input <= 180) {
                    longitude = input
                } else {
                    println("Invalid longitude. Please enter a number between -180 and 180.")
                }
            } catch (e: Exception) {
                println("Invalid input. Please enter a valid number.")
            }
        }
        
        return Pair(latitude, longitude)
    }
    
    fun getDateRangeInput(): Pair<LocalDate, LocalDate> {
        var startDate: LocalDate? = null
        var endDate: LocalDate? = null
        
        while (startDate == null) {
            print("Enter start date (YYYY-MM-DD): ")
            try {
                val input = readlnOrNull()
                startDate = LocalDate.parse(input ?: "")
            } catch (e: DateTimeParseException) {
                println("Invalid date format. Please use YYYY-MM-DD.")
            }
        }
        
        while (endDate == null) {
            print("Enter end date (YYYY-MM-DD): ")
            try {
                val input = readlnOrNull()
                val parsedDate = LocalDate.parse(input ?: "")
                
                if (parsedDate >= startDate) {
                    endDate = parsedDate
                } else {
                    println("End date must be after or equal to start date.")
                }
            } catch (e: Exception) {
                println("Invalid date format. Please use YYYY-MM-DD.")
            }
        }
        
        return Pair(startDate, endDate)
    }
    
    fun displayResults(analysis: TemperatureAnalysis, latitude: Double, longitude: Double) {
        println("\n---- Weather Analysis Results ----")
        println("Location: Latitude $latitude, Longitude $longitude")
        
        if (analysis.maxTemperature != null) {
            println("Maximum temperature: ${analysis.maxTemperature.temperature}°C on ${analysis.maxTemperature.time}")
        } else {
            println("Maximum temperature: Not available")
        }
        
        if (analysis.minTemperature != null) {
            println("Minimum temperature: ${analysis.minTemperature.temperature}°C on ${analysis.minTemperature.time}")
        } else {
            println("Minimum temperature: Not available")
        }
        
        if (analysis.averageTemperature != null) {
            println("Average temperature: ${String.format("%.2f", analysis.averageTemperature)}°C")
        } else {
            println("Average temperature: Not available")
        }
        
        println("--------------------------------")
    }
    
    fun displayError(message: String) {
        println("\n---- Error ----")
        println(message)
        println("---------------")
    }
}