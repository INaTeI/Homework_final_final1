package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.domain.model.Country

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val code: String,
    val name: String,
    val capital: String,
    val region: String,
    val population: Long,
    val flag: String,
    val cachedAt: Long = 0L
) {
    fun toCountry() = Country(code, name, capital, region, population, flag)

    companion object {
        fun fromCountry(country: Country, cachedAt: Long = 0L) =
            CountryEntity(
                country.code,
                country.name,
                country.capital,
                country.region,
                country.population,
                country.flag,
                cachedAt
            )

        fun fromNetwork(country: Country, cachedAt: Long = System.currentTimeMillis()) =
            fromCountry(country, cachedAt)
    }
}
