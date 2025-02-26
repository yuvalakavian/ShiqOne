package com.example.shiqone.model

import com.google.gson.annotations.SerializedName

// Updated data class with proper type safety
// Assuming "hourly" contains time and temperature values

data class Weather(
    @SerializedName("hourly")
    val hourlyWeather: Hourly
)

data class Hourly(
    val time: List<String>,
    @SerializedName("temperature_2m") val temperature2m: List<Double>
)

//
//"latitude": 52.52,
//"longitude": 13.419,
//"elevation": 44.812,
//"generationtime_ms": 2.2119,
//"utc_offset_seconds": 0,
//"timezone": "Europe/Berlin",
//"timezone_abbreviation": "CEST",
//"hourly": {
//    "time": ["2022-07-01T00:00", "2022-07-01T01:00", "2022-07-01T02:00", ...],
//    "temperature_2m": [13, 12.7, 12.7, 12.5, 12.5, 12.8, 13, 12.9, 13.3, ...]
//},
//"hourly_units": {
//    "temperature_2m": "°C"
//}