package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Country

object BundledCountriesSeed {
    val countries = listOf(
        Country("UA", "Ukraine", "Kyiv", "Europe", 40_000_000L, "🇺🇦"),
        Country("PL", "Poland", "Warsaw", "Europe", 38_000_000L, "🇵🇱"),
        Country("DE", "Germany", "Berlin", "Europe", 83_240_525L, "🇩🇪"),
        Country("FR", "France", "Paris", "Europe", 67_391_582L, "🇫🇷"),
        Country("GB", "United Kingdom", "London", "Europe", 67_215_293L, "🇬🇧"),
        Country("US", "United States", "Washington, D.C.", "Americas", 329_484_123L, "🇺🇸"),
        Country("BR", "Brazil", "Brasilia", "Americas", 212_559_409L, "🇧🇷"),
        Country("AR", "Argentina", "Buenos Aires", "Americas", 45_808_747L, "🇦🇷"),
        Country("JP", "Japan", "Tokyo", "Asia", 125_836_021L, "🇯🇵"),
        Country("CN", "China", "Beijing", "Asia", 1_402_112_000L, "🇨🇳"),
        Country("IN", "India", "New Delhi", "Asia", 1_380_004_385L, "🇮🇳"),
        Country("EG", "Egypt", "Cairo", "Africa", 102_334_403L, "🇪🇬"),
        Country("MA", "Morocco", "Rabat", "Africa", 36_910_558L, "🇲🇦"),
        Country("ZA", "South Africa", "Pretoria", "Africa", 59_308_690L, "🇿🇦"),
        Country("AU", "Australia", "Canberra", "Oceania", 25_687_041L, "🇦🇺")
    )
}
