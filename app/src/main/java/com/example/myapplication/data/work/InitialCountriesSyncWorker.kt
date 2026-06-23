package com.example.myapplication.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.domain.repository.CountryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class InitialCountriesSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val countryRepository: CountryRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (
            countryRepository.hasCachedCountries() &&
            countryRepository.getLastCacheTimestamp() > 0L
        ) {
            return Result.success()
        }

        return try {
            countryRepository.refreshCountries()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
