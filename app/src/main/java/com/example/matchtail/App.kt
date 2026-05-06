package com.example.matchtail

import android.app.Application
import android.content.Context

/**
 * Project Owners: Hadar Lachmy, Tal Eyal
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        private var appContext: Context? = null

        val context: Context
            get() = appContext ?: throw Exception("Context not found")
    }
}