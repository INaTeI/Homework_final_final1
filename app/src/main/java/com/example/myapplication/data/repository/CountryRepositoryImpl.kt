package com.example.myapplication.data.repository

import com.example.myapplication.data.api.CountriesApi
import com.example.myapplication.data.dto.CountryDto
import com.example.myapplication.data.local.CountriesDao
import com.example.myapplication.data.local.CountryEntity
import com.example.myapplication.domain.model.Country
import com.example.myapplication.domain.preferences.AppPreferences
import com.example.myapplication.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val api: CountriesApi,
    private val dao: CountriesDao,
    private val preferences: AppPreferences
) : CountryRepository {

    override fun observeCountries(): Flow<List<Country>> {
        return dao.observeCached().map { list ->
            list.map { it.toCountry() }
        }
    }

    override suspend fun refreshCountries() {
        val now = System.currentTimeMillis()
        val networkData = api.getCountries().data
            ?: throw InvalidCountriesResponseException()
        val countries = networkData.map { (code, dto) ->
            dto.toCountry(code)
        }
        if (countries.isEmpty()) {
            throw EmptyCountriesResponseException()
        }

        dao.replaceCached(countries.map { CountryEntity.fromNetwork(it, now) })
        preferences.setLastSyncTimestamp(now)
    }

    override suspend fun getCountry(code: String): Country {
        dao.getByCode(code)?.takeIf { it.cachedAt > 0L }?.toCountry()?.let { return it }

        throw CountryNotCachedException(code)
    }

    override suspend fun hasCachedCountries(): Boolean {
        return dao.getCached().isNotEmpty()
    }

    override suspend fun getLastCacheTimestamp(): Long {
        return dao.getLastCacheTimestamp() ?: 0L
    }

    private fun CountryDto.toCountry(code: String) = Country(
        name = country,
        code = code,
        capital = "Unknown",
        region = region,
        population = 0,
        flag = "https://flagcdn.com/w320/${code.lowercase(Locale.US)}.png"
    )
}

class EmptyCountriesResponseException : IllegalStateException("API returned no countries")

class InvalidCountriesResponseException : IllegalStateException("API returned invalid countries response")

class CountryNotCachedException(code: String) : NoSuchElementException(
    "Country $code is not available in the local cache"
)
