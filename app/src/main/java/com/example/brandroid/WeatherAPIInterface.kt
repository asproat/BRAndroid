package com.example.brandroid

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherAPIInterface {

    @GET("/cities")
    suspend fun searchCities(@Query("search") search: String?): Response<ResponseBody>

    @GET("/cities/{id}")
    suspend fun getCityWeather(@Path("id") id: Int): Response<ResponseBody>

    @GET("/cities/{id}/radar")
    suspend fun getCityRadar(@Path("id") id: Int): Response<ResponseBody>

}