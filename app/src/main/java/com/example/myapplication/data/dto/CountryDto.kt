package com.example.myapplication.data.dto



data class CountriesResponseDto(
    val data: Map<String, CountryDto> = emptyMap()
)

data class CountryDto(
    val country: String,
    val region: String
)
