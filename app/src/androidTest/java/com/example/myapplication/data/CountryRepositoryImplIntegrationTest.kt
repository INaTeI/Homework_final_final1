package com.example.myapplication.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.CountryEntity
import com.example.myapplication.data.repository.CountryRepositoryImpl
import com.example.myapplication.data.dto.CountryDto
import com.example.myapplication.domain.model.Country
import com.example.myapplication.domain.preferences.AppPreferences
import com.example.myapplication.fake.FakeCountriesApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CountryRepositoryImplIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: CountryRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val preferences = mockk<AppPreferences>(relaxed = true)
        coEvery { preferences.setLastSyncTimestamp(any()) } returns Unit
        repository = CountryRepositoryImpl(FakeCountriesApi(), database.countriesDao(), preferences)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun refreshCountries_persistsDataInRoom_andCanBeReadAgain() = runTest {
        repository.refreshCountries()

        val fromDao = database.countriesDao().getCached()
        assertEquals(2, fromDao.size)

        val readAgain = database.countriesDao().getByCode("UA")
        assertEquals("Ukraine", readAgain?.name)
    }

    @Test
    fun observeCountries_emitsPersistedSequenceAfterRefresh() = runTest {
        repository.observeCountries().test {
            assertEquals(emptyList<Country>(), awaitItem())

            repository.refreshCountries()
            val firstLoaded = awaitItem()
            assertEquals(2, firstLoaded.size)
            assertEquals("UA", firstLoaded.first { it.code == "UA" }.code)

            val persisted = database.countriesDao().getCached()
            assertEquals(firstLoaded.map { it.code }.toSet(), persisted.map { it.code }.toSet())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun seedRows_areNotReturnedAsCache_untilNetworkRefreshPersistsRealCache() = runTest {
        database.countriesDao().insert(
            CountryEntity.fromCountry(
                Country(
                    code = "JP",
                    name = "Japan",
                    capital = "Tokyo",
                    region = "Asia",
                    population = 125_000_000L,
                    flag = "https://example.com/jp.png"
                )
            )
        )

        repository.observeCountries().test {
            assertEquals(emptyList<Country>(), awaitItem())

            repository.refreshCountries()
            val cached = awaitItem()
            assertEquals(2, cached.size)
            assertTrue(cached.none { it.code == "JP" })
            assertTrue(database.countriesDao().getAllIncludingSeed().any { it.code == "JP" && it.cachedAt == 0L })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshCountries_replacesPreviousCachedSnapshot() = runTest {
        repository.refreshCountries()
        assertEquals(setOf("UA", "PL"), database.countriesDao().getCached().map { it.code }.toSet())

        val preferences = mockk<AppPreferences>(relaxed = true)
        coEvery { preferences.setLastSyncTimestamp(any()) } returns Unit
        val updatedRepository = CountryRepositoryImpl(
            FakeCountriesApi(
                countries = mapOf(
                    "UA" to CountryDto(country = "Ukraine", region = "Europe"),
                    "DE" to CountryDto(country = "Germany", region = "Europe")
                )
            ),
            database.countriesDao(),
            preferences
        )

        updatedRepository.refreshCountries()

        assertEquals(setOf("UA", "DE"), database.countriesDao().getCached().map { it.code }.toSet())
    }

    @Test
    fun refreshCountries_whenApiFails_keepsExistingRoomCache() = runTest {
        repository.refreshCountries()

        val preferences = mockk<AppPreferences>(relaxed = true)
        val failingRepository = CountryRepositoryImpl(
            FakeCountriesApi(failAll = true),
            database.countriesDao(),
            preferences
        )

        try {
            failingRepository.refreshCountries()
        } catch (exception: IOException) {
            assertEquals(setOf("UA", "PL"), database.countriesDao().getCached().map { it.code }.toSet())
            return@runTest
        }

        error("Expected refreshCountries to fail without clearing existing cache")
    }

    @Test
    fun getLastCacheTimestamp_usesOnlyRealRoomCache() = runTest {
        val preferences = mockk<AppPreferences>(relaxed = true)
        coEvery { preferences.getLastSyncTimestamp() } returns 999L
        val repository = CountryRepositoryImpl(
            FakeCountriesApi(),
            database.countriesDao(),
            preferences
        )

        assertEquals(0L, repository.getLastCacheTimestamp())

        repository.refreshCountries()

        assertTrue(repository.getLastCacheTimestamp() > 0L)
    }

    @Test
    fun refreshCountries_whenApiFailsAndCacheIsEmpty_keepsRoomEmpty() = runTest {
        val preferences = mockk<AppPreferences>(relaxed = true)
        val failingRepository = CountryRepositoryImpl(
            FakeCountriesApi(failAll = true),
            database.countriesDao(),
            preferences
        )

        try {
            failingRepository.refreshCountries()
        } catch (exception: IOException) {
            assertEquals(emptyList<Country>(), database.countriesDao().getCached().map { it.toCountry() })
            return@runTest
        }

        error("Expected refreshCountries to fail when API is unavailable and cache is empty")
    }

    @Test
    fun observeCountries_returnsCachedRoomData_whenNextLaunchHasNoNetwork() = runTest {
        repository.refreshCountries()

        val preferences = mockk<AppPreferences>(relaxed = true)
        val offlineRepository = CountryRepositoryImpl(
            FakeCountriesApi(failAll = true),
            database.countriesDao(),
            preferences
        )

        offlineRepository.observeCountries().test {
            val cached = awaitItem()
            assertEquals(2, cached.size)
            assertTrue(cached.map { it.code }.containsAll(listOf("UA", "PL")))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
