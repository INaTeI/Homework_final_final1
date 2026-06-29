package com.example.myapplication.data.api


import com.example.myapplication.data.dto.CountriesResponseDto
import retrofit2.http.GET


interface CountriesApi {

    @GET("countries?")
    suspend fun getCountries(): CountriesResponseDto

}
