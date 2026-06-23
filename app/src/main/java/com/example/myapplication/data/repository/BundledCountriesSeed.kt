package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Country

object BundledCountriesSeed {
    val countries = listOf(
        Country("UA", "Ukraine", "Kyiv", "Europe", 40_000_000L, "https://flagcdn.com/w320/ua.png"),
        Country("PL", "Poland", "Warsaw", "Europe", 38_000_000L, "https://flagcdn.com/w320/pl.png"),
        Country("DE", "Germany", "Berlin", "Europe", 83_240_525L, "https://flagcdn.com/w320/de.png"),
        Country("FR", "France", "Paris", "Europe", 67_391_582L, "https://flagcdn.com/w320/fr.png"),
        Country("GB", "United Kingdom", "London", "Europe", 67_215_293L, "https://flagcdn.com/w320/gb.png"),
        Country("US", "United States", "Washington, D.C.", "Americas", 329_484_123L, "https://flagcdn.com/w320/us.png"),
        Country("BR", "Brazil", "Brasilia", "Americas", 212_559_409L, "https://flagcdn.com/w320/br.png"),
        Country("AR", "Argentina", "Buenos Aires", "Americas", 45_808_747L, "https://flagcdn.com/w320/ar.png"),
        Country("JP", "Japan", "Tokyo", "Asia", 125_836_021L, "https://flagcdn.com/w320/jp.png"),
        Country("CN", "China", "Beijing", "Asia", 1_402_112_000L, "https://flagcdn.com/w320/cn.png"),
        Country("IN", "India", "New Delhi", "Asia", 1_380_004_385L, "https://flagcdn.com/w320/in.png"),
        Country("EG", "Egypt", "Cairo", "Africa", 102_334_403L, "https://flagcdn.com/w320/eg.png"),
        Country("MA", "Morocco", "Rabat", "Africa", 36_910_558L, "https://flagcdn.com/w320/ma.png"),
        Country("ZA", "South Africa", "Pretoria", "Africa", 59_308_690L, "https://flagcdn.com/w320/za.png"),
        Country("AU", "Australia", "Canberra", "Oceania", 25_687_041L, "https://flagcdn.com/w320/au.png")
    )
}