package com.example.locationinbackgroundwithforegroundservice

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.locationinbackgroundwithforegroundservice.location.LocationService.Companion.CHANNELID
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class LocationApp:Application() {

    @Inject lateinit var hiltWorkerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()

        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(hiltWorkerFactory).build())





        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){

            val channel = NotificationChannel(CHANNELID, "Location", NotificationManager.IMPORTANCE_LOW)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}