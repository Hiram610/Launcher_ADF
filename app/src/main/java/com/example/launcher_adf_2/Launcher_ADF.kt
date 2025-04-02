package com.example.launcher_adf_2

import android.app.Application

class Launcher_ADF : Application() {

    companion object {
        lateinit var prefs: Prefers
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefers(applicationContext)
    }
}