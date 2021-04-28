package com.example.brandroid

import kotlinx.serialization.Serializable

@Serializable
data class CityWeather (
    val weather: WeatherResult,
    val city: City
)