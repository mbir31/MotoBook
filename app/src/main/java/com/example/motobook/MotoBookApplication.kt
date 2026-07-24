package com.example.motobook

import android.app.Application
import com.example.motobook.di.AppContainer

class MotoBookApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
