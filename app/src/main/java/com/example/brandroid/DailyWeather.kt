package com.example.brandroid

import kotlinx.serialization.Serializable

@Serializable
data class DailyWeather(
    val high: Int,
    val low: Int,
    val weatherType: String,
    val dayOfTheWeek: Int,
    val hourlyWeather: List<HourlyWeather>
)