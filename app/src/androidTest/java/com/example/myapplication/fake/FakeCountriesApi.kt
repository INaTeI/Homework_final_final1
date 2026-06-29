package com.example.myapplication.fake

import com.example.myapplication.data.api.CountriesApi
import com.example.myapplication.data.dto.CountriesResponseDto
import com.example.myapplication.data.dto.CountryDto

class FakeCountriesApi(
    private val countries: Map<String, CountryDto>? = defaultCountries,
    private val failAll: Boolean = false,
    private val failOnce: Boolean = false
) : CountriesApi {

    var allRequestCount = 0
        private set

    override suspend fun getCountries(): CountriesResponseDto {
        allRequestCount++
        if (failAll) throw java.io.IOException("API unavailable")
        if (failOnce && allRequestCount == 1) throw java.io.IOException("API unavailable")
        return CountriesResponseDto(countries)
    }

    companion object {
        val defaultCountries = mapOf(
            "UA" to CountryDto(
                country = "Ukraine",
                region = "Europe"
            ),
            "PL" to CountryDto(
                country = "Poland",
                region = "Europe"
            )
        )
    }
}
