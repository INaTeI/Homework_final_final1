package com.example.myapplication.data.api


import com.example.myapplication.data.dto.CountriesResponseDto
import retrofit2.http.GET


interface CountriesApi {

    @GET("countries?limit=300")
    suspend fun getCountries(): CountriesResponseDto

}
