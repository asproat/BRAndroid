package com.example.brandroid

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class City(
    val name : String,
    val asciiname : String,
    val dem: Int,
    val longitude: Float,
    val latitude: Float,
    @SerializedName("country code")
    val countrycode: String,
    @SerializedName("admin1 code")
    val admin1code: String,
    @SerializedName("admin2 code")
    val admin2code: Int,
    @SerializedName("admin3 code")
    val admin3code: Int?,
    @SerializedName("admin4 code")
    val admin4code: String,
    val geonameid: Int,
    val population: Int,
    @SerializedName("feature class")
    val featureclass: String,
    @SerializedName("feature code")
    val featurecode: String,
    val timezone: String,
    val elevation: Int,
    val cc2: String,
    @SerializedName("modification date")
    val modificationdate: String,
    val imageURLs: ImageURLs
)