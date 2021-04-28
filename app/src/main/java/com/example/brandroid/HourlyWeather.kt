package com.example.brandroid

import kotlinx.serialization.Serializable

@Serializable
data class HourlyWeather(
    val windSpeed: Float,
    val humidity: Float,
    val rainChance: Float,
    val temperature: Int,
    val hour: Int,
    val weatherType: String
)