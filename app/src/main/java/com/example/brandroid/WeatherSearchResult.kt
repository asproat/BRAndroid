package com.example.brandroid

import kotlinx.serialization.Serializable

@Serializable
data class WeatherSearchResult(
        val totalCitiesFound : Int,
        val startIndex: Int,
        val cities : List<City>
)
