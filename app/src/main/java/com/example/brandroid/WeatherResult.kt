package com.example.brandroid

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResult(
    val id: Int,
    val days: List<DailyWeather>
)