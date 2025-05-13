package org.example.ui

import kotlinx.datetime.LocalDate
import org.example.models.TemperatureAnalysis

class Location(
    val latitude: Double,
    val longitude: Double
)

fun getLocationInput(): Location {
    var latitude: Double? = null
    while (latitude == null) {
        print("Enter latitude (-90 to 90): ")
        try {
            val input = readln().toDoubleOrNull()
            if (input != null && input in -90.0..90.0) {
                latitude = input
            } else {
                println("Invalid latitude. Please enter a number between -90 and 90.")
            }
        } catch (_: CharacterCodingException) {
            println("Invalid input. Please enter a valid number.")
        }
    }

    var longitude: Double? = null
    while (longitude == null) {
        print("Enter longitude (-180 to 180): ")
        try {
            val input = readln().toDoubleOrNull()
            if (input != null && input in -180.0..180.0) {
                longitude = input
            } else {
                println("Invalid longitude. Please enter a number between -180 and 180.")
            }
        } catch (_: CharacterCodingException) {
            println("Invalid input. Please enter a valid number.")
        }
    }

    return Location(latitude, longitude)
}

fun getDateRangeInput(): ClosedRange<LocalDate> {
    var startDate: LocalDate? = null
    var endDate: LocalDate? = null

    while (startDate == null) {
        print("Enter start date (YYYY-MM-DD): ")
        try {
            startDate = LocalDate.parse(readln())
        } catch (_: IllegalArgumentException) {
            println("Invalid date format. Please use YYYY-MM-DD.")
        } catch (_: CharacterCodingException) {
            println("Invalid input. Please enter a valid input.")
        }
    }

    while (endDate == null) {
        print("Enter end date (YYYY-MM-DD): ")
        try {
            val parsedDate = LocalDate.parse(readln())

            if (parsedDate >= startDate) {
                endDate = parsedDate
            } else {
                println("End date must be after or equal to start date.")
            }
        } catch (_: IllegalArgumentException) {
            println("Invalid date format. Please use YYYY-MM-DD.")
        } catch (_: CharacterCodingException) {
            println("Invalid input. Please enter a valid input.")
        }
    }

    return startDate..endDate
}

fun displayResults(analysis: TemperatureAnalysis?, location: Location) {
    println("\n---- Weather Analysis Results ----")
    println("Location: Latitude ${location.latitude}, Longitude ${location.longitude}")
    if (analysis == null) {
        println("No analysis available.")
    } else {
        println("Maximum temperature: ${analysis.maxTemperature.temperature}°C on ${analysis.maxTemperature.time}")
        println("Minimum temperature: ${analysis.minTemperature.temperature}°C on ${analysis.minTemperature.time}")
        println("Average temperature: ${String.format("%.2f", analysis.averageTemperature)}°C")
    }
    println("--------------------------------")
}

fun displayError(message: String) {
    println("\n---- Error ----")
    println(message)
    println("---------------")
}
