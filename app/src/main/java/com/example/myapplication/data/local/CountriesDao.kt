package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CountriesDao {

    @Query("SELECT * FROM countries WHERE cachedAt > 0 ORDER BY name")
    abstract fun observeCached(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE cachedAt > 0 ORDER BY name")
    abstract suspend fun getCached(): List<CountryEntity>

    @Query("SELECT * FROM countries WHERE code = :code LIMIT 1")
    abstract suspend fun getByCode(code: String): CountryEntity?

    @Query("SELECT MAX(cachedAt) FROM countries WHERE cachedAt > 0")
    abstract suspend fun getLastCacheTimestamp(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(countries: List<CountryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(country: CountryEntity)

    @Query("DELETE FROM countries WHERE cachedAt > 0")
    abstract suspend fun deleteCached()

    @Transaction
    open suspend fun replaceCached(countries: List<CountryEntity>) {
        deleteCached()
        insertAll(countries)
    }

    @Query("SELECT * FROM countries ORDER BY name")
    abstract suspend fun getAllIncludingSeed(): List<CountryEntity>
}
