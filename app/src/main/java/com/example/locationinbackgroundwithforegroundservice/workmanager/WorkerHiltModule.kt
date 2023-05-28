package com.example.locationinbackgroundwithforegroundservice.workmanager

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
object WorkerHiltModule {


    @Provides
    fun providesMainRepo():MainWorkerRepository{
        return MainWorkerRepository()
    }



}