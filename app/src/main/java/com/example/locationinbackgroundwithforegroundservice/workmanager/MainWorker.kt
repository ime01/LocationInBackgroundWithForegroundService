package com.example.locationinbackgroundwithforegroundservice.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class MainWorker @AssistedInject constructor (
    @Assisted context:Context,
    @Assisted workerParameters: WorkerParameters,
    private val mainWorkerRepository: MainWorkerRepository):Worker(context, workerParameters) {

    override fun doWork(): Result {
        mainWorkerRepository.updateDataFromNetwork()
        return Result.success()
    }
}