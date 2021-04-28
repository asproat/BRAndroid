package com.example.brandroid

import kotlinx.serialization.Serializable

@Serializable
data class ImageURLs(
    val iOSImageURLs: ImageURL,
    val androidImageURLs: AndroidImageURLs
)