package com.wh.sunnyweather

import android.app.Application
import android.content.Context

class SunnyWeatherApplication : Application() {

    companion object {
        const val TOKEN = "9dMoy7Rzpd0FQsrq"
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}