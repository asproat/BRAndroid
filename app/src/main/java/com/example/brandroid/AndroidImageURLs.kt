package com.example.brandroid

import kotlinx.serialization.Serializable

@Serializable
data class AndroidImageURLs(
    val xhdpiImageURL: String?,
    val hdpiImageURL: String?,
    val mdpiImageURL: String?
)